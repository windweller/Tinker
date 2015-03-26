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

  if (targetColumn.isEmpty) fatal("target column cannot be empty.")

  //this method is overridden
  //by specific format
  check(this)

}