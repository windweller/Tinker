package core.structure

import scala.collection.mutable

/**
 * Created by anie on 8/26/2015
 *
 * just provide a blueprint for Doc to work on
 *
 * passed in from DataContainer
 *
 * @param stringColumn string columns are used to read in
 *
 */
case class Schema(stringColumn: Option[Int] = None,
                  stringColumnWithName: Option[String] = None,
                  stringColumnsWithName: Option[Vector[String]] = None,
                  stringColumns: Option[Vector[Int]] = None,
                  ignoreColumn: Option[Int] = None,
                  ignoreColumnWithName: Option[String] = None,
                  ignoreColumns: Option[Vector[Int]] = None,
                  ignoreColumnsWithName: Option[Vector[String]] = None) {

  //TODO: add ignoreRange to Schema

  lazy val stringColumnNames: Option[mutable.HashSet[String]] = extractVector(stringColumn, stringColumnWithName, stringColumns, stringColumnsWithName)
  lazy val ignoreColumnNames: Option[mutable.HashSet[String]] = extractVector(ignoreColumn, ignoreColumnWithName, ignoreColumns, ignoreColumnsWithName)

  /**
   * Check if user wants a certain value
   * to be coerced into string, because Tinker
   * automatically cast all numeric values (including IDs)
   * to Double
   * @param header
   * @return
   */
  def coerceString(header: String): Boolean = if (stringColumnNames.nonEmpty) stringColumnNames.get.contains(header) else false

  /**
   * If a column should be ignored, return
   * @param header
   * @return
   */
  def ifIgnore(header: String): Boolean = if (ignoreColumnNames.nonEmpty) ignoreColumnNames.get.contains(header) else false

  def ifnotIgnore(header: String): Boolean = !ifIgnore(header)

  private[this] def extractVector(c1: Option[Int], c2: Option[String],
                                  c3: Option[Vector[Int]], c4: Option[Vector[String]]) = {
    if (c1.nonEmpty || c2.nonEmpty)
      getSingleIntStringOption(c1, c2).map(mutable.HashSet(_))
    else if (c3.nonEmpty || c4.nonEmpty)
      getMultipleIntStringOption(c3, c4)
    else None
  }

  protected def getSingleIntStringOption(a1: Option[Int], a2: Option[String]): Option[String] = {
    if (a1.nonEmpty) a1.map(f => f.toString)
    else a2
  }

  protected def getMultipleIntStringOption(a1: Option[Vector[Int]], a2: Option[Vector[String]]): Option[mutable.HashSet[String]] = {
    val hashset = mutable.HashSet.empty[String]
    if (a1.nonEmpty) {
      a1.get.foreach(elem => hashset.add(elem.toString))
    }
    else a2.get.foreach(hashset.add)
    Some(hashset)
  }

}