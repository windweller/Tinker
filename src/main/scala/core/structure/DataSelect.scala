package core.structure

import core.TypedRow
import core.structure.Index.IndexRange

/**
 * Created by anie on 7/15/2015
 *
 * This is to replace DataStructure
 * It is a truncated version of DataStructure
 *
 * Also the name gets changed, because this is more about
 *
 * need to support IndexRange though
 */
class DataSelect(targetColumn: Option[Int] = None,
                 targetColumnWithName: Option[String] = None,
                 targetColumns: Option[Vector[Int]] = None,
                 targetColumnsWithName: Option[Vector[String]] = None,
                 val targetRange: Option[IndexRange] = None){

  /**
   * @deprecated for legacy methods, future use please choose targets
   */
    lazy val target: Option[String] = getSingleIntStringOption(targetColumn, targetColumnWithName)

    lazy val targets: Option[Vector[String]] = {
      val target = getSingleIntStringOption(targetColumn, targetColumnWithName)
      if (target.nonEmpty) target.map(t => Vector(t))
      else getMultipleIntStringOption(targetColumns, targetColumnsWithName)
    }
  /**
   * Pass in DataStruct because we want to take
   * care of the targetRange
   * @param st
   * @return
   */
  def getTargets(st: DataStruct): Vector[String] = {
    if (targets.nonEmpty)
      targets.get
    else
    { //handle targetRange
      //TODO: to be implemented!
      //use the st passed in
      Vector("")
    }
  }

  def getTargetValue(row: TypedRow): Option[String] = {
    getSingleIntStringOptionValue(targets.map(e => e.head), row)
  }

  def getTargetsValue(row: TypedRow): Option[Vector[String]] = {
    getMultipleIntStringOptionValue(targetColumns, targetColumnsWithName, row)
  }

  protected def getSingleIntStringOption(a1: Option[Int], a2: Option[String]): Option[String] = {
    if (a1.nonEmpty) a1.map(f => f.toString)
    else a2
  }

  protected def getSingleIntStringOptionValue(key: Option[String], row: TypedRow): Option[String] = {
    key.map(k => row(k))
  }

  protected def getMultipleIntStringOption(a1: Option[Vector[Int]], a2: Option[Vector[String]]): Option[Vector[String]] = {
    if (a1.nonEmpty) Some(a1.get.map(e => e.toString))
    else a2
  }

  protected def getMultipleIntStringOptionValue(a1: Option[Vector[Int]], a2: Option[Vector[String]], row: TypedRow): Option[Vector[String]] = {
    if (a1.nonEmpty) Some(a1.get.map(e => row(e.toString)))
    else if (a2.nonEmpty) Some(a2.get.map(e => row(e)))
    else None
  }

}

object DataSelect {

  def apply(targetColumn: Option[Int] = None,
            targetColumnWithName: Option[String] = None,
            targetColumns: Option[IndexedSeq[Int]] = None,
            targetColumnsWithName: Option[IndexedSeq[String]] = None): DataSelect
  = new DataSelect(targetColumn = targetColumn, targetColumnWithName = targetColumnWithName)

  def apply(targetRange: IndexRange): DataSelect = new DataSelect(targetRange = Some(targetRange))
}