package core

import core.structure.DataStruct

import scala.language.implicitConversions

/**
 * Created by anie on 7/28/2015
 *
 * A typedrow simulate the actions of a hashmap
 * but only stores vector
 *
 * In order for this to work, TypedRow can only be changed
 * along with DataStruct, this class simulate HashMap but is doing
 * double duty
 *
 * Some functions might not be saving a lot key strokes
 * those are done in the purpose of performance
 *
 */
class TypedRow(sv: Option[Vector[String]] = None,
               fv: Option[Vector[Double]] = None) {

  var stringVector: Vector[String] = sv.getOrElse(Vector.empty[String])
  var featureVector: Vector[Double] = fv.getOrElse(Vector.empty[Double])

  def +=(param: Double): TypedRow = {
    featureVector = featureVector :+ param
    this
  }

  def +=(param: String): TypedRow = {
    stringVector = stringVector :+ param
    this
  }

  /**
   * If the header already exists in struct,
   * we don't need to put it in again
   *
   * We don't do lookup, but count the size
   * Since we store both differently, we count
   * size differently
   *
   * None of them actually checks if the header matches
   * existing header (because iterator processing functions are
   * the same and don't change)
   *
   * @param row the actual row passed in
   * @param struct data structure
   * @return
   */
  def headerExist(row: StringVector)(implicit struct: DataStruct): Boolean = {
//    println("left side: " + struct.stringHeader.size)
//    println("right side: " + (stringVector.size + row.sv.length))
    if (struct.stringHeader.size < stringVector.size + row.sv.length) false
    else true
  }

  def headerExist(row: DoubleVector)(implicit struct: DataStruct): Boolean = {
    if (struct.featureHeader.size < featureVector.size + row.dv.length) false
    else true
  }

  def headerExist(value: String)(implicit struct: DataStruct): Boolean =
    if (struct.stringHeader.size < stringVector.size) false else true

  def headerExist(value: Double)(implicit struct: DataStruct): Boolean =
    if (struct.featureHeader.size < featureVector.size) false else true


  /**
   * eC performance
   *
   * We only add headers if those headers were never added before
   *
   * @param arg append element to featureVector (e.g: hs += "featureName" -> 2.0)
   * @return the position of this inserted element
   */
  def +=(arg: StringDoubleTuple)(implicit struct: DataStruct): TypedRow = {
    featureVector = featureVector :+ arg._2
    if (!headerExist(arg._2)) {
      struct.addToFeatureHeader(arg._1, featureVector.length - 1)
    }
    this
  }

  /**
   * eC performance
   * @param arg append element to stringVector
   * @return the position of this inserted element
   */
  def +=(arg: StringStringTuple)(implicit struct: DataStruct): TypedRow = {
    stringVector = stringVector :+ arg._2
    if (!headerExist(arg._2)) {
      struct.addToStringHeader(arg._1, stringVector.length - 1)
    }
    this
  }

  /**
   * This simulates adding a collection to TypedRow
   *
   * Maybe we don't need to check header twice here,
   * but this class could be used by other people
   *
   * @param hs main argument, first names for each new cell, then value
   * @param headerGroupName optional just to add to headerGroup
   * @return
   */
  def ++=(hs: StringDoubleMap, headerGroupName: Option[String])(implicit struct: DataStruct): TypedRow = {
    var addingHeader = false

    hs.sdm.foreach { pair =>
      val pos: Int = featureVector.length
      //add to featureVector
      featureVector = featureVector :+ pair._2
      //add to header
      if (!headerExist(pair._2)) {
        struct.addToFeatureHeader(pair._1, pos)
        addingHeader = true
      }
    }

    //add to headerGroup if there is such
    //and if they don't exit
    if (addingHeader)
      headerGroupName.foreach(hn => struct.addToHeaderGroup(hn, hs.sdm.keys.toVector)) //maybe toVector is slow, careful

    this
  }

  //I don't know which one I'll use so it's better to have two
  //performance-wise they should be similar
  def ++=(arg: StringVecDoubleVecTuple, headerGroupName: Option[String])(implicit struct: DataStruct): TypedRow = {
    val addingHeader = !headerExist(arg._1)

    arg._1.indices.foreach { i =>
      val pos: Int = featureVector.length
      //add to feature vector, double
      featureVector = featureVector :+ arg._2.dv(i)
      //add to header
      if (addingHeader)
        struct.addToFeatureHeader(arg._1(i), pos)
    }

   this
  }

  def ++=(hs: StringStringMap, headerGroupName: Option[String])(implicit struct: DataStruct): TypedRow = {
    var addingHeader = false

    hs.ssm.foreach { pair =>
      val pos: Int = stringVector.length
      //add to featureVector
      stringVector = stringVector :+ pair._2
      //add to header
      if (!headerExist(pair._2)) {
        addingHeader = true
        struct.addToFeatureHeader(pair._1, pos)
      }

    }
    //add to headerGroup if there is such
    if (addingHeader)
      headerGroupName.foreach(hn => struct.addToHeaderGroup(hn, hs.ssm.keys.toVector)) //maybe toVector is slow, careful
    this
  }

  def ++=(arg: StringVecStringVecTuple, headerGroupName: Option[String])(implicit struct: DataStruct): TypedRow = {
    val addingHeader = !headerExist(arg._1)

    arg._1.indices.foreach { i =>
      val pos: Int = stringVector.length
      //add to feature vector, double
      stringVector = stringVector :+ arg._2.sv(i)
      //add to header
      if (addingHeader)
        struct.addToStringHeader(arg._1(i), pos)
    }
    this
  }

  //Last two addition functions might be used most often

  /**
   * This function is only called if the outside
   * procedure invokes headerExist() and make sure header exists
   *
   * @param arg
   * @return
   */
  def ++=(arg: DoubleVector): TypedRow = {
    arg.dv.foreach(d => featureVector = featureVector :+ d)
    this
  }

  def ++=(arg: StringVector): TypedRow = {
    arg.sv.foreach(s => stringVector = stringVector :+ s)
    this
  }

}

object TypedRow {

  def apply(sv: Option[Vector[String]] = None,
            fv: Option[Vector[Double]] = None) = new TypedRow(sv, fv)

  //those are there to combat type erasure
  object Implicits {

    import scala.collection.mutable

    case class DoubleVector(dv: Vector[Double])
    case class StringVector(sv: Vector[String])

    case class StringDoubleTuple(_1: String, _2: Double)
    case class StringStringTuple(_1: String, _2: String)

    case class StringDoubleMap(sdm: mutable.HashMap[String, Double])
    case class StringStringMap(ssm: mutable.HashMap[String, String])

    case class StringVecDoubleVecTuple(_1: Vector[String], _2: DoubleVector)
    case class StringVecStringVecTuple(_1: Vector[String], _2: StringVector)

    implicit def tdv(dv: Vector[Double]): DoubleVector = DoubleVector(dv)
    implicit def tsv(sv: Vector[String]): StringVector = StringVector(sv)

    implicit def sdt(sdt: (String, Double)): StringDoubleTuple = StringDoubleTuple(sdt._1, sdt._2)
    implicit def sst(sst: (String, String)): StringStringTuple = StringStringTuple(sst._1, sst._2)

    implicit def sdm(sdm: mutable.HashMap[String, Double]): StringDoubleMap = StringDoubleMap(sdm)
    implicit def ssm(ssm: mutable.HashMap[String, String]): StringStringMap = StringStringMap(ssm)

    implicit def svdvt(svdvt: (Vector[String], Vector[Double])): StringVecDoubleVecTuple = StringVecDoubleVecTuple(svdvt._1, svdvt._2)
    implicit def svsvt(svsvt: (Vector[String], Vector[String])): StringVecStringVecTuple = StringVecStringVecTuple(svsvt._1, svsvt._2)

  }

}