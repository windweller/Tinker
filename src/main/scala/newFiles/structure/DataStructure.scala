package newFiles.structure

import newFiles.RowTypes.NormalRow
import utils.FailureHandle

/**
 * Created by Aimingnie on 4/25/15
 *
 * This could be optimized to do a upfront transformation
 * instead of transforming each time
 *
 * Right now it seems like DataStructure is not for
 * DataContainers, but more for algorithms
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
                             keepColumns: Option[IndexedSeq[Int]] = None,
                             keepColumnsWithNames: Option[IndexedSeq[String]] = None) extends FailureHandle with StructureUtils {

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

  def getKeepColumns: Option[IndexedSeq[String]]  = {
    getMultipleIntStringOption(keepColumns, keepColumnsWithNames)
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

  def getKeepColumnsValue(row: NormalRow): Option[IndexedSeq[String]] = {
    getMultipleIntStringOptionValue(keepColumns, keepColumnsWithNames, row)
  }

}

trait StructureUtils {

  /*
  *  Util functions
  */

  protected def getSingleIntStringOption(a1: Option[Int], a2: Option[String]): Option[String] = {
    if (a1.nonEmpty) a1.map(f => f.toString)
    else a2
  }

  protected def getSingleIntStringOptionValue(a1: Option[Int], a2: Option[String], row: NormalRow): Option[String] = {
    val key = getSingleIntStringOption(a1, a2)
    key.map(k => row(k))
  }

  protected def getMultipleIntStringOption(a1: Option[IndexedSeq[Int]], a2: Option[IndexedSeq[String]]): Option[IndexedSeq[String]] = {
    if (a1.nonEmpty) Some(a1.get.map(e => e.toString))
    else a2
  }

  protected def getMultipleIntStringOptionValue(a1: Option[IndexedSeq[Int]], a2: Option[IndexedSeq[String]], row: NormalRow): Option[IndexedSeq[String]] = {
    if (a1.nonEmpty) Some(a1.get.map(e => row(e.toString)))
    else if (a2.nonEmpty) Some(a2.get.map(e => row(e)))
    else None
  }

}