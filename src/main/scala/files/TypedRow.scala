package files

/**
 * Created by anie on 7/28/2015
 *
 * A typedrow simulate the actions of a hashmap
 * but only stores vector
 *
 * all actions will require implicit parameter DataHeader
 *
 */
class TypedRow(val stringVector: Option[Vector[String]] = None,
                val featureVector: Option[Vector[Double]] = None)(implicit val header: DataHeader) {



}

object TypedRow {

  /**
   * TypedRow object has multiple apply()
   * method to create, retrieve information
   */

  def apply(num: Int) = {

  }


}