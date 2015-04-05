package files.structure

import files.DataContainer
import files.structure.specifics.FormatChecks

/**
 * Created by anie on 3/22/2015.
 * This should be descriptive for the
 * data structure of the Array/Map
 *
 * Data Structure as Array[Tuple] because
 * Scala doesn't allow map traversing
 *
 * Basically, id and label column can all be empty (clustering, or just no id),
 * but target column must exist
 *
 * targetColumns and keepColumns do not support name mapping
 * because for files with a lot of columns, having name mapping does not make sense
 *
 * we'll cross-reference with headerMap
 *
 * Right now we encourage names...instead of column number
 *
 * You only need to mock up the data structure for algorithms
 *
 * @param attributeColumns IndexedSeq[Int] guarantees the style like (1 to 10) ++ Vector(13)
 *                         it's columns that you can/should manipulate
 *                         such as compression and so on
 *
 * @param keepColumns columns you want to keep in the result. By default, Tinker
 *                    drop all irrelevant columns, but if this is activated, then only those
 *                    columns will be kept (except for id, attr, label columns)
 */
abstract class DataStructure(val idColumn: Option[Int] = None,
                              val idColumnWithName: Option[String] = None,
                              val attributeColumns: Option[IndexedSeq[Int]] = None,
                              val attributeColumnsWithName: Option[IndexedSeq[String]] = None,
                              val labelColumn: Option[Int] = None,
                              val labelColumnWithName: Option[String] = None,
                              val targetColumn: Option[IndexedSeq[Int]] = None,
                              val keepColumns: Option[IndexedSeq[Int]] = None) extends FormatChecks{

  var size = 0

  if (targetColumn.isEmpty) fatal("target column cannot be empty.")

  //we forbid both because then we won't be able to calculate total size of this structure
  if (idColumn.nonEmpty && idColumnWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")
  if (attributeColumns.nonEmpty && attributeColumnsWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")
  if (labelColumn.nonEmpty && labelColumnWithName.nonEmpty) fatal("Either use name or id number. Don't use both.")

  if (idColumn.nonEmpty) size += 1
  if (idColumnWithName.nonEmpty) size += 1
  if (labelColumn.nonEmpty) size += 1
  if (labelColumnWithName.nonEmpty) size += 1

  if (attributeColumns.nonEmpty) size += attributeColumns.get.size
  if (attributeColumnsWithName.nonEmpty) size += attributeColumnsWithName.get.size
  if (targetColumn.nonEmpty) size += targetColumn.get.size
  if (keepColumns.nonEmpty) size += keepColumns.get.size

  //this method is overridden
  //by specific format
  check(Left(this))
}

object DataStructureTypes {
  type Structure = Either[DataStructure, DataStructureValue]
}