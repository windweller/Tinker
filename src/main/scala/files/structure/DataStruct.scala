package files.structure

import scala.collection.mutable

/**
 * DataStruct goes with each DataContainer file
 * It stores headers, and have vital functions for:
 * 1. Access those values
 * 2. Decide printing orders (stringHeader first, featureHeader later)
 */
class DataStruct {

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

  def addToStringHeader(columnName: String, vectorPos: Int): Unit = stringHeader += (columnName -> vectorPos)
  def addToFeatureHeader(columnName: String, vectorPos: Int): Unit = featureHeader += (columnName -> vectorPos)

}
