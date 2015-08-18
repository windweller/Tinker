package files

import core.DataContainer
import core.structure.DataStruct
import utils.FailureHandle

/**
 * Created by anie on 8/17/2015
 *
 * Very similar to old Doc but absorbs the functionality
 * of FileMapIterator, and pass down the header collecting
 * function to FileIterator
 *
 */
trait NewDoc extends DataContainer with FailureHandle {

  /* ===== Abstract methods/variables ===== */

  def typesuffix: Vector[String]

  def parse: (String) => Vector[String] //this method is being overridden by different types

  /* ===== End of Abstract methods/variables ===== */

  val ds: DataStruct = new DataStruct



}
