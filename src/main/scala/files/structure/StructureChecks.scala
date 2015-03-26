package files.structure

import files.DataContainer
import utils.FailureHandle

/**
 * Created by anie on 3/25/2015.
 *
 * this is a mix in trait for all modules that
 * requires the usage of both data structure and data container
 */
trait StructureChecks extends FailureHandle{

  def validityCheck(data: DataContainer)(struct: DataStructure): Unit = {
    if (data.dataIteratorPure.next().length < struct.size) fatal("Struct columns don't match with data's column")
  }

}
