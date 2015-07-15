package parser

import files.structure.DataStructure

/**
 * Created by anie on 7/14/2015.
 */
object Struct {

  def apply(idColumn: Option[Int] = None,
            idColumnWithName: Option[String] = None,
            targetColumn: Option[Int] = None,
            targetColumnWithName: Option[String] = None,
            ignoreColumns: Option[IndexedSeq[Int]] = None,
            ignoreColumnsWithName: Option[IndexedSeq[String]] = None): DataStructure

      = new DataStructure(idColumn = idColumn, idColumnWithName = idColumnWithName, targetColumn = targetColumn,
                            targetColumnWithName = targetColumnWithName, ignoreColumns = ignoreColumns,
                            ignoreColumnsWithName = ignoreColumnsWithName)
}