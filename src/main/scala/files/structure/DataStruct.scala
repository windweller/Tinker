package files.structure

import files.RowTypes._
import files.structure.Index.IndexRange

/**
 * Created by anie on 7/15/2015
 *
 * This is to replace DataStructure
 * It is a truncated version of DataStructure
 */
class DataStruct(targetColumn: Option[Int] = None,
                 targetColumnWithName: Option[String] = None,
                 targetColumns: Option[IndexedSeq[Int]] = None,
                 targetColumnsWithName: Option[IndexedSeq[String]] = None,
                 val targetRange: Option[IndexRange] = None) extends StructureUtils {
  lazy val target: Option[String] = getSingleIntStringOption(targetColumn, targetColumnWithName)
  lazy val targets: Option[IndexedSeq[String]] = getMultipleIntStringOption(targetColumns, targetColumnsWithName)

  def getTargetValue(row: NormalRow): Option[String] = {
    getSingleIntStringOptionValue(target, row)
  }

  def getTargetsValue(row: NormalRow): Option[IndexedSeq[String]] = {
    getMultipleIntStringOptionValue(targetColumns, targetColumnsWithName, row)
  }

}

object DataStruct {

  def apply(targetColumn: Option[Int] = None,
            targetColumnWithName: Option[String] = None,
            targetColumns: Option[IndexedSeq[Int]] = None,
            targetColumnsWithName: Option[IndexedSeq[String]] = None): DataStruct
  = new DataStruct(targetColumn = targetColumn, targetColumnWithName = targetColumnWithName)

  def apply(targetRange: IndexRange): DataStruct = new DataStruct(targetRange = Some(targetRange))
}