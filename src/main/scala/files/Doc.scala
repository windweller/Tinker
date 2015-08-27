package files

import core.{DataContainer, RowTypes}
import RowTypes._
import core.structure.DataStruct
import utils.OptionToParameter.Implicits._
import utils._

import scala.collection.mutable

/**
 * Extends this means that
 * you are working with an Actual file
 * instead of HTTP, Stream, or any other
 * format.
 *
 * It combines old FileTypes and Doc.
 *
 * This is a downstream of DataContainer,
 * because it implements DataContainer's certain methods and types
 *
 * Doc also automatically
 */
trait Doc extends DataContainer with FailureHandle {

  /**** Abstract methods/variables ****/

  def typesuffix: Vector[String]

  def parse: (String) => Vector[String] //this method is being overriden by differnt types

  /**** Concrete implementations ****/

  //get all files
  def files: Map[String, FileIterator] = FileMapIterator.getFileMap(f, header, fuzzyMatch)(typesuffix)

  //allow peaking, assuming all files share same structure
  lazy private[this] val file = files.head._2

  /**
   * this new method forces HashMap on header/nonHeader structure
   * only inefficient part is the toString, toInt conversion
   *
   * It has two behaviors, both of them greatly impact the whole data representation
   * #1: If header.size > real column.size, it chops off header (because real data is more important)
   * #2: If header.size < real column.size, it will make up the header as Integer index, starting from 0 as usual
   */
  val headerString: Vector[String] =
    if (file.headerRaw.nonEmpty) {
      val vec = parse(file.headerRaw)
      val vec2 = parse(file.firstRow)

      if (vec.size < vec2.size)
        vec ++ (vec.size to vec2.size).map(e => e.toString)
      else vec
    }
    else Vector.iterate("0", parse(file.firstRow).length)(pos => (pos.toInt + 1).toString)


  //We fill the data struct according to schema
  implicit val ds: DataStruct = {
    //a method that generates two hashmaps that fill up featureHeader and stringHeader
    //and return a DataStruct

  }


  protected def readFileIterator[T](transform: (String) => T, file: FileIterator): Iterator[T] = file.map(l => transform(l))

  /**
   * For smaller memory footprint, and faster performance
   * Scala mutable HashMap has proven to be the best collection (better than Java)
   * @return
   */
  def dataIterators: mutable.HashMap[String, RowIterator] = {
    val map = mutable.HashMap.empty[String, RowIterator]
    files.foreach(e => map.put(e._1, readFileIterator[NormalRow]((line) => mutable.HashMap(headerString.zip(parse(line)): _*), e._2)))
    map
  }

  override def iteratorMap = dataIterators

}
