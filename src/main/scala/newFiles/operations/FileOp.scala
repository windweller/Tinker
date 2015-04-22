package newFiles.operations

import newFiles.DataContainer
import newProcessing.Operation


/**
 * Created by anie on 4/18/2015.
 */
trait FileOp extends DataContainer with Operation {

  def combine(data2: DataContainer): DataContainer with FileOp = {
    iterators ++= data2.iterators
    this
  }


}
