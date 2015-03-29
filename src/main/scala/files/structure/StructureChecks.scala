package files.structure

import files.DataContainer
import utils.FailureHandle

/**
 * Created by anie on 3/25/2015.
 *
 * this is a mix in trait for all modules that
 * requires the usage of both data structure and data container
 */
object StructureChecks extends FailureHandle{

  def check(data: DataContainer)(struct: DataStructure): Unit = {
    validityCheck(data)(struct)
    headerMatchIterator(data)(struct)
  }

  //if Iterator returns without header, and user uses header to select columns
  //throw fatal error
  def headerMatchIterator(data: DataContainer)(struct: DataStructure): Unit = {
    if (!data.header)
      if (struct.attributeColumnsWithName.nonEmpty || struct.idColumnWithName.nonEmpty || struct.labelColumnWithName.nonEmpty)
        fatal("If the file does not contain header, you cannot select columns using strings.")
  }

  def validityCheck(data: DataContainer)(struct: DataStructure): Unit = {
    if (data.dataIteratorPure.next().length < struct.size) fatal("Struct columns don't match with data's column")
  }

}
