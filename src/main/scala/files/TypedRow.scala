package files

import scala.language.implicitConversions

/**
 * Created by anie on 7/28/2015
 *
 * A typedrow simulate the actions of a hashmap
 * but only stores vector
 *
 * all actions will require implicit parameter DataHeader
 *
 * In order for this to work, TypedRow can only be changed
 * along with DataStruct
 *
 */
class TypedRow(sv: Option[Vector[String]] = None,
               fv: Option[Vector[Double]] = None) {

  import TypedRow.Implicits._

  var stringVector: Vector[String] = sv.getOrElse(Vector.empty[String])
  var featureVector: Vector[Double] = fv.getOrElse(Vector.empty[Double])

  /**
   * eC performance
   * @param d append element to featureVector
   * @return the position of this inserted element
   */
  def +=(d: Double): Int = {
    featureVector = featureVector :+ d
    featureVector.length - 1
  }
  /**
   * eC performance
   * @param s append element to stringVector
   * @return the position of this inserted element
   */
  def +=(s: String): Int = {
    stringVector = stringVector :+ s
    stringVector.length - 1
  }

  def ++=(d: DoubleVector): Range = {
    val startIndex: Int = featureVector.length //first elem place
    featureVector = featureVector ++: d.dv
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