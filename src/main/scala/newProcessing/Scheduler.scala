package newProcessing

import newFiles.RowTypes.{RowIterator, NormalRow}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * This stores sequence
 */
abstract class Scheduler(workerCount: Int) extends Operation {

  //this keeps a history of iterators
  protected val opSequence: ArrayBuffer[RowIterator] = ArrayBuffer.empty[RowIterator]

  def clean(): Unit = opSequence.clear()


}
