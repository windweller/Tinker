package core.structure

import scala.collection.mutable

/**
 * DataStruct goes with each DataContainer file
 * It stores headers, and have vital functions for:
 * 1. Access those values
 * 2. Decide printing orders (stringHeader first, featureHeader later)
 */
class DataStruct(sHeader: Option[mutable.HashMap[String, Int]] = None,
                  fHeader: Option[mutable.HashMap[String, Int]] = None) {

  /*
 * Since it's mapping to TypedRow
 * It should have both featureHeader, and StringHeader
 *
 * They are implemented as HashMap to achieve O(1) lookup
 * performance
 */

  //this maintains the order of the string
  //don't know if I want to store types as well, probably not needed
  var orderedHeader = Vector.empty[String]

  //"column name" -> 5 (vector position)
  val stringHeader = sHeader.getOrElse(mutable.HashMap.empty[String, Int])
  val featureHeader = fHeader.getOrElse(mutable.HashMap.empty[String, Int])

  //"tregexFeatures" -> Vector("VP>NP", "NP>NP>PP")
  //headerGroup provides a way to quickly access required header
  val headerGroups = mutable.HashMap.empty[String, Vector[String]]

  //this does not add to string/featureHeader, must be used in conjunction
  //with other methods
  def addToHeaderGroup(headerName: String, headers: Vector[String]): Unit = {
    headerGroups += (headerName -> headers)
  }

  /**
   * Aux function to add to Vector
   * @param columnName the name to add to hashmap (key)
   * @param vectorPos corresponding position in the vector of this value
   */
  def addToStringHeader(columnName: String, vectorPos: Int): Unit = {
    stringHeader += (columnName -> vectorPos)
    orderedHeader = orderedHeader :+ columnName
  }
  def addToFeatureHeader(columnName: String, vectorPos: Int): Unit = {
    featureHeader += (columnName -> vectorPos)
    orderedHeader = orderedHeader :+ columnName
  }

  //this gurantees one traversal
  def addToStringHeader(columnName: Vector[String], vectorPos: Range): Unit =
                          addVectorToHeader(stringHeader, columnName, vectorPos)

  def addToFeatureHeader(columnName: Vector[String], vectorPos: Range): Unit =
                          addVectorToHeader(featureHeader, columnName, vectorPos)

  private[this] def addVectorToHeader(headerA: mutable.HashMap[String, Int],
                                      columnName: Vector[String], vectorPos: Range): Unit = {
    vectorPos.indices.foreach { i =>
      headerA += columnName(i) -> vectorPos(i)
      orderedHeader = orderedHeader :+ columnName(i)
    }
  }

}
