package core

import core.TypedRow.ValueWithType
import core.structure.DataStruct._
import core.structure.{DataSelect, DataStruct, Schema}
import utils.FailureHandle

import scala.collection.{AbstractIterator, Iterator, mutable}
import scala.util.{Failure, Success, Try}

/**
 * Created by Aimingnie on 8/31/15
 * Provide a row (vector) that stores any as value
 * and shallow cast types
 *
 * This class DOES NOT handle parsing
 * correct types. That's handled by Doc trait
 *
 * Passing in the reference to struct at creation time
 * not method time
 *
 * We are also ensuring ValueWithType info is never stored
 * in TypedRow
 *
 * NOTICE: We are now doing type casting inside TypedRow
 * it is no longer a burden of Doc. Schema is passed in (not stored)
 * to help guide the process
 */
class TypedRow()(implicit val struct: DataStruct) extends FailureHandle {

  //Actual row does not have type info
  //type info is only required to construct
  var row: Vector[Any] = Vector.empty[Any]

  def exists(header: String): Boolean = {
    struct.headerMap.get(header).isEmpty
  }

  /**
   * This doesn't check for repetition on header
   * instead, this will replace the value of the
   * original header if there are two headers
   * @param arg _1: header, _2: value
   * @param sm it's suggested to declare implicit val schema outside
   *           to make += and ++= behave more like HashMap()
   * @return
   */
  def +=(arg: (String, String))(implicit sm: Schema = Schema()): TypedRow = {
    val vwt = getValueWithType(arg._1, arg._2)(sm)
    row = row :+ vwt.v
    struct += arg._1 -> CellInfo(row.length - 1, vwt.ty)
    this
  }

  def ++=(hm: mutable.HashMap[String, String])
         (implicit sm: Schema = Schema()): TypedRow = {

    import DataStruct._

    hm.foreach { case (header, value) =>
      val pos: Int = row.length

      //know if there's type coersion instruction
      //before converting type
      val vwt = getValueWithType(header, value)(sm)

      //add to row
      row = row :+ vwt.v
      //add to header
      struct += (header -> CellInfo(pos, vwt.ty))
    }

    this
  }

  /**
   * This serves as a constructor for internal use
   * passing in Any value. Used by filter
   *
   * Those values have been pre-determined type
   *
   * position has to be re-calculated
   * but types stay the same
   *
   * Struct is shared, and we only need to update position
   *
   * mainly used to create a new TypedRow from an old one
   * @param hm
   * @return
   */
  protected def addValuePairs(hm: mutable.HashMap[String, Any]): TypedRow = {
    hm.foreach(addValuePair)
    this
  }

  protected def addValuePair(pair: (String, Any)): TypedRow = {
    struct.updatePosOf(pair._1, row.length)  //update new position
    row = row :+ pair._2
    this
  }

  private[this] def getValueWithType(header: String, value: String)(sm: Schema) = {
    if (!sm.coerceString(header)) typeCast(value)
    else ValueWithType(value, string)
  }

  // ===== Casting functions =====

  private[this] def typeCast(value: String): ValueWithType = {

    val intoption = cast[Int](value)(v => v.toInt)
    val doubleoption = cast[Double](value)(v => v.toDouble)

    if (intoption.nonEmpty)
      ValueWithType(intoption.get, int)
    else if (doubleoption.nonEmpty)
      ValueWithType(doubleoption.get, double)
    else
      ValueWithType(value, string)

  }

  //type cast
  private[this] def cast[A](value: String)(castFun: String => A): Option[A] = {
    slientGetOrElse(Try{
      castFun.apply(value)
    })
  }

  //this uses asInstanceOf method
  private[this] def directCast[A](value: Any): A = {
    value.asInstanceOf[A]
  }

  private[this] def slientGetOrElse[A](r: Try[A]): Option[A] = {
    if (r.isSuccess) Some(r.get)
    else None
  }

  // ====== Accessing functions =====

  def get(header: String): Option[Element] = {
    //implicitly[Applicative[Option]].map2(struct.getPosOf(header), struct.getTypeOf(header))((pos, tpe) => Element(pos, tpe))
    struct.getCellInfoOf(header).map(c => Element(c.pos, c.ty))
  }

  //this one does not handle exception
  private[this] def getValue(header: String): Any = row(struct.getPosOf(header).get)

  def getAsDouble(header: String): Double = {
    getValue(header).asInstanceOf[Double]
  }

  //this faciliates coersion
  def getAsString(header: String): String = {
    val value = getValue(header)
    Try(directCast[String](value)) match {
      case Success(v) => v
      case Failure(ex) => value.toString
    }
  }

  /**
   * This simulate a normal HashMap's getting element
   * method, this coerce all the result to be string
   *
   * This caters to certain legacy methods
   * @param h
   */
  def apply(h: String): String = getAsString(h)

  // ======= Collection Functional Methods =====

  /**
   * We only allow filtering based on header
   *
   * DataStruct must be updated (fields not fitting has to be removed)
   * Then all of the fields (rest of them) are updated in `addValuePairs` method
   *
   * @param p a function, select all that satisfies this predicate (true)
   *
   * @return return a new TypedRow, this function is idempotent
   */
  def filter(p: ((String, Any)) => Boolean): TypedRow = {

    val st = new DataStruct()

    val tr = new TypedRow()(st) //new datastruct, will be filled up in

    struct.headerMap.foreach { case (header, cellInfo) =>
      if (p.apply(header, row(cellInfo.pos)))
        tr.addValuePair((header, row(cellInfo.pos)))
    }

    tr
  }

  /**
   * This is specifically for CSVOutput and all output
   * modules when they have both select and ignore as option values
   *
   * @param select
   * @param ignore
   * @return
   */
  def select(select: Option[DataSelect], ignore: Option[DataSelect]): Option[TypedRow] = {

    if (select.nonEmpty && ignore.nonEmpty) fatal("can't choose both select and ignore")

    //if select is chosen
    val s = select.map(s => filter {case (header, value) => s.getTargets(struct).contains(header)})

    //if ignore is chosen
    val i = ignore.map(s => filter {case (header, value) => !s.getTargets(struct).contains(header)})

    triOption(s, i)
  }

  //return a or b (whether one has true value in them), or the original row, if neither
  private[this] def triOption(a: Option[TypedRow], b:Option[TypedRow]): Option[TypedRow] = {
    if (a.nonEmpty) a
    else if (b.nonEmpty) b
    else Some(this)
  }

  //all keys are strings
  def keysIterator: Iterator[String] = struct.headersIterator

  //value iterator will follow an order
  def valuesIterator: Iterator[Any] = new AbstractIterator[Any] {

    val hit = keysIterator

    override def hasNext: Boolean = hit.hasNext

    override def next(): Any = row(struct.getPosOf(hit.next()).get)
  }

  def tupleIterator: Iterator[(String, Any)] = new AbstractIterator[(String, Any)] {

    val hit = keysIterator

    override def hasNext: Boolean = hit.hasNext

    override def next(): (String, Any) = {
      val h = hit.next()
      (h, row(struct.getPosOf(h).get))
    }
  }

  /**
   * Using this method will result in losing the order
   * @return
   */
  def toMap: mutable.HashMap[String, Any] = {
    val hit = keysIterator
    val m = mutable.HashMap.empty[String, Any]

    hit.foreach(h => m.put(h, row(struct.getPosOf(h).get)))
    m
  }

  def toTuple: Vector[(String, Any)] = {
    tupleIterator.toVector
  }

  def values: Vector[Any] = row

  def headers: Vector[String] = struct.orderedHeader

  //for println() functions
  override def toString: String = {
    toMap.toString()
  }
}

object TypedRow {
  case class ValueWithType(v: Any, ty: Symbol)

  def apply(sm: Schema = Schema(),
            mp: Option[mutable.HashMap[String, String]] = None)(implicit struct: DataStruct): TypedRow = {
    val tr = new TypedRow()(struct)
    implicit val ism = sm  //so implicit requirement is satisfied
    mp.foreach(m => tr ++= m)
    tr
  }
}

/**
 * When you get an element from TypedRow,
 * you get an Element class wrapped around the value
 * this way, it won't waste additional storage, but
 * provide more information than primitive types
 * @param v the real value
 * @param tpe the type as string
 */
case class Element(v: Any, tpe: Symbol)