package files.structure

import files.RowTypes._
import utils.FailureHandle
import scala.collection.mutable

/**
 * Created by Aimingnie on 4/25/15
 *
 * This could be optimized to do a upfront transformation
 * instead of transforming each time
 *
 * Right now it seems like DataStructure is not for
 * DataContainers, but more for algorithms
 *
 * @param ignoreColumn this will be taken into account for output generation
 */
class DataStructure(idColumn: Option[Int] = None,
                             idColumnWithName: Option[String] = None,
                             val attributeColumns: Option[IndexedSeq[Int]] = None,
                             val attributeColumnsWithName: Option[IndexedSeq[String]] = None,
                             labelColumn: Option[Int] = None,
                             labelColumnWithName: Option[String] = None,
                             targetColumn: Option[Int] = None,
                             targetColumnWithName: Option[String] = None,
                             val targetColumns: Option[IndexedSeq[Int]] = None,
                             targetColumnsWithName: Option[IndexedSeq[String]] = None,
                             ignoreColumn: Option[Int] = None,
                             ignoreColumnWithName: Option[String] = None,
                             val ignoreColumns: Option[IndexedSeq[Int]] = None,
                             val ignoreColumnsWithName: Option[IndexedSeq[String]] = None,
                             keepColumns: Option[IndexedSeq[Int]] = None,
                             keepColumnsWithNames: Option[IndexedSeq[String]] = None) extends FailureHandle with StructureUtils {

  //we only allow one
  if (idColumn.nonEmpty && idColumnWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")
  if (attributeColumns.nonEmpty && attributeColumnsWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")
  if (labelColumn.nonEmpty && labelColumnWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")

  lazy val target: Option[String] = getSingleIntStringOption(targetColumn, targetColumnWithName)
  lazy val targets: Option[IndexedSeq[String]] = getMultipleIntStringOption(targetColumns, targetColumnsWithName)
  lazy val id: Option[String] = getSingleIntStringOption(idColumn, idColumnWithName)
  lazy val ignore: Option[String] = getSingleIntStringOption(ignoreColumn, ignoreColumnWithName)
  lazy val label: Option[String] = getSingleIntStringOption(labelColumn, labelColumnWithName)
  lazy val keeps: Option[IndexedSeq[String]] = getMultipleIntStringOption(keepColumns, keepColumnsWithNames)
  lazy val ignores: Option[IndexedSeq[String]] = getMultipleIntStringOption(ignoreColumns, ignoreColumnsWithName)

  /*
   * iterator
   */

  //could be empty
  lazy val values: Vector[Option[String]] = Vector(target, id, ignore, label)
  lazy val multivalues: Vector[Option[IndexedSeq[String]]] = Vector(keeps)

  /*
   * Actual Value Getters (go with .getOrElse)
   */
  def getIdValue(row: NormalRow): Option[String] = {
    getSingleIntStringOptionValue(id, row)
  }

  def getTargetValue(row: NormalRow): Option[String] = {
    getSingleIntStringOptionValue(target, row)
  }

  def getLabelValue(row: NormalRow): Option[String] = {
    getSingleIntStringOptionValue(label, row)
  }

  def getKeepColumnsValue(row: NormalRow): Option[IndexedSeq[String]] = {
    getMultipleIntStringOptionValue(keepColumns, keepColumnsWithNames, row)
  }

  /**
   * Get Key, Value pairs
   */

  def getKeepColumnsKeyValuePairs(row: NormalRow): Option[mutable.HashMap[String, String]] = {
      val map = mutable.HashMap.empty[String, String]
      if (keepColumns.nonEmpty) keepColumns.get.foreach(e => map.put(e.toString, row(e.toString)))
      else if (keepColumnsWithNames.nonEmpty) keepColumnsWithNames.get.foreach(e => map.put(e, row(e)))

      if (map.nonEmpty) Some(map) else None
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

  protected def getSingleIntStringOptionValue(key: Option[String], row: NormalRow): Option[String] = {
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