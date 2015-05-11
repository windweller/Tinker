package newFiles.structure

import newFiles.rowTypes.NormalRow
import utils.FailureHandle

/**
 * Created by Aimingnie on 4/25/15
 */
abstract class DataStructure(idColumn: Option[Int] = None,
                             idColumnWithName: Option[String] = None,
                             val attributeColumns: Option[IndexedSeq[Int]] = None,
                             val attributeColumnsWithName: Option[IndexedSeq[String]] = None,
                             labelColumn: Option[Int] = None,
                             labelColumnWithName: Option[String] = None,
                             targetColumn: Option[Int] = None,
                             targetColumnWithName: Option[String] = None,
                             val targetColumns: Option[IndexedSeq[Int]] = None,
                             ignoreColumn: Option[Int] = None,
                             ignoreColumnWithName: Option[String] = None,
                             val ignoreColumns: Option[IndexedSeq[Int]] = None,
                             val keepColumns: Option[IndexedSeq[Int]] = None) extends FailureHandle {

  def predefinedCheck(): Unit

  //we only allow one
  if (idColumn.nonEmpty && idColumnWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")
  if (attributeColumns.nonEmpty && attributeColumnsWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")
  if (labelColumn.nonEmpty && labelColumnWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")

  //predefined check
  predefinedCheck()

  /*
   * Column Name Getters
  */

  def getTarget: Option[String] = {
    getSingleIntStringOption(targetColumn, targetColumnWithName)
  }

  def getId: Option[String] = {
    getSingleIntStringOption(idColumn, idColumnWithName)
  }

  def getIgnore: Option[String] = {
    getSingleIntStringOption(ignoreColumn, ignoreColumnWithName)
  }

  def getLabel: Option[String] = {
    getSingleIntStringOption(labelColumn, labelColumnWithName)
  }

  /*
   * Actual Value Getters (go with .getOrElse)
   */
  def getIdValue(row: NormalRow): Option[String] = {
    getSingleIntStringOptionValue(idColumn, idColumnWithName, row)
  }

  def getTargetValue(row: NormalRow): Option[String] = {
    getSingleIntStringOptionValue(targetColumn, targetColumnWithName,row)
  }

  def getLabelValue(row: NormalRow): Option[String] = {
    getSingleIntStringOptionValue(labelColumn, labelColumnWithName, row)
  }

  /*
   *  Util functions
   */

  private[this] def getSingleIntStringOption(a1: Option[Int], a2: Option[String]): Option[String] = {
    if (a1.nonEmpty) a1.map(f => f.toString)
    else a2
  }

  private[this] def getSingleIntStringOptionValue(a1: Option[Int], a2: Option[String], row: NormalRow): Option[String] = {
    val key = getSingleIntStringOption(a1, a2)
    key.map(k => row(k))
  }

}
