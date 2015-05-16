package newProcessing

import newFiles.RowTypes.{RowIterator, NormalRow}
import newProcessing.buffers.BufferConfig

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * This stores sequence
 */
abstract class Scheduler(workerCount: Int)(implicit val bufferConfig: BufferConfig) extends Operation {

  val workerNum = workerCount

  val config = bufferConfig

  //this keeps a history of iterators
  val opSequence: mutable.Stack[RowIterator] = mutable.Stack()

  def clean(): Unit = opSequence.clear()

}
