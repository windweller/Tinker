package core.operations

import core.DataContainer
import utils.FailureHandle

/**
 * Created by Aimingnie on 9/20/15
 *
 * This replaces old FileOp because
 * all operations can run on different origins
 */
trait DataOp extends FailureHandle {

  this: DataContainer =>



}
