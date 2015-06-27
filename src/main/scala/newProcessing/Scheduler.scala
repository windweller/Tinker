package newProcessing

import files.RowTypes.{RowIterator, NormalRow}
import newProcessing.buffers.BufferConfig

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * This stores sequence of Iterators
 *
 * It also provides customization to BufferConfig
 */
abstract class Scheduler(workerCount: Int)(implicit val bufferConfig: BufferConfig = BufferConfig()) extends Operation {

  val workerNum = workerCount

  val config = bufferConfig

  //this keeps a history of iterators
  val opSequence: mutable.Stack[RowIterator] = mutable.Stack()

  def clean(): Unit = opSequence.clear()

}
