package newFiles.structure

import newFiles.rowTypes.NormalRow
import utils.FailureHandle

/**
 * Created by Aimingnie on 4/25/15.
 */
abstract class DataStructure(val idColumn: Option[Int] = None,
                             val idColumnWithName: Option[String] = None,
                             val attributeColumns: Option[IndexedSeq[Int]] = None,
                             val attributeColumnsWithName: Option[IndexedSeq[String]] = None,
                             val labelColumn: Option[Int] = None,
                             val labelColumnWithName: Option[String] = None,
                             val targetColumn: Option[Int] = None,
                             val targetColumnWithName: Option[String] = None,
                             val targetColumns: Option[IndexedSeq[Int]] = None,
                             val keepColumns: Option[IndexedSeq[Int]] = None) extends FailureHandle {

  def predefinedCheck(): Unit

  //this already covers most basic NLP tasks
  if (targetColumns.isEmpty &&
    targetColumn.isEmpty &&
    targetColumnWithName.isEmpty) fatal("target column cannot be empty.")

  //we only allow one
  if (idColumn.nonEmpty && idColumnWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")
  if (attributeColumns.nonEmpty && attributeColumnsWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")
  if (labelColumn.nonEmpty && labelColumnWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")

  //predefined check
  predefinedCheck()

  def getTarget: String = {
    if (targetColumn.nonEmpty) targetColumn.get.toString
    else targetColumnWithName.get
  }

  //so it goes nicely with .getOrElse
  def getId(row: NormalRow): Option[String] = {
    val key = if (idColumn.nonEmpty) idColumn.map(f => f.toString)
    else idColumnWithName
    key.map(k => row(k))
  }

}
