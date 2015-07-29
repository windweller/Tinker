package files

import scala.collection.mutable

/**
 * Created by Aimingnie on 7/29/15
 *
 * This file stores header
 * Header map to TypedRow
 */
class DataHeader {

  /*
   * Since it's mapping to TypedRow
   * It should have both featureHeader, and StringHeader
   */

  //"column name" -> 5 (vector position)
  val stringHeader = mutable.HashMap.empty[String, Int]
  val featureHeader = mutable.HashMap.empty[String, Int]

  //"tregexFeatures" -> Vector("VP>NP", "NP>NP>PP")
  //headerGroup provides a way to quickly access required header
  val headerGroups = mutable.HashMap.empty[String, Vector[String]]

}
