package files

import files.structure.DataStruct

import scala.collection.immutable.HashMap
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
 */
class TypedRow(sv: Option[Vector[String]] = None,
               fv: Option[Vector[Double]] = None) {

  import TypedRow.Implicits._

  var stringVector: Vector[String] = sv.getOrElse(Vector.empty[String])
  var featureVector: Vector[Double] = fv.getOrElse(Vector.empty[Double])

  /**
   * eC performance
   * @param arg append element to featureVector (e.g: hs += "featureName" -> 2.0)
   * @return the position of this inserted element
   */
  def +=(arg: (String, Double))(implicit struct: DataStruct): TypedRow = {
    featureVector = featureVector :+ arg._2
    struct.addToFeatureHeader(arg._1, featureVector.length - 1)
    this
  }
  /**
   * eC performance
   * @param arg append element to stringVector
   * @return the position of this inserted element
   */
  def +=(arg: (String, String))(implicit struct: DataStruct): TypedRow = {
    stringVector = stringVector :+ arg._2
    struct.addToStringHeader(arg._1, stringVector.length - 1)
    this
  }

  /**
   * This simulates adding a collection to TypedRow
   * @param hs main argument, first names for each new cell, then value
   * @param headerGroupName optional just to add to headerGroup
   * @param struct
   * @return
   */
  def ++=(hs: HashMap[String, Double], headerGroupName: Option[String] = None)(implicit struct: DataStruct): Range = {
    val startIndex: Int = featureVector.length //first elem place
    hs.valuesIterator.foreach(d => featureVector = featureVector :+ d) //update all through iterating
    val endIndex = featureVector.length - 1 //last elem place

    startIndex to endIndex
  }

  //I don't know which one I'll use so it's better to have two
  //performance-wise they should be similar
  def ++=(arg: (Vector[String], DoubleVector), headerGroupName: Option[String] = None)(implicit struct: DataStruct): Range = {
    val startIndex: Int = featureVector.length //first elem place
    featureVector = featureVector ++: arg._2.dv
    val endIndex = featureVector.length - 1 //last elem place

    startIndex to endIndex
  }

  def ++=(s: StringVector): Range = {
    val startIndex: Int = stringVector.length //first elem place
    stringVector = stringVector ++: s.sv
    val endIndex = stringVector.length - 1 //last elem place
    startIndex to endIndex
  }

}

object TypedRow {

  def apply(sv: Option[Vector[String]] = None,
            fv: Option[Vector[Double]] = None) = new TypedRow(sv, fv)

  object Implicits {
    case class DoubleVector(dv: Vector[Double])
    case class StringVector(sv: Vector[String])

    implicit def tdv(dv: Vector[Double]): DoubleVector = DoubleVector(dv)
    implicit def tsv(sv: Vector[String]): StringVector = StringVector(sv)
  }

}