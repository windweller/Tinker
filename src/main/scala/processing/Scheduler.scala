package processing

import files.RowTypes.RowIterator
import processing.buffers.BufferConfig

import scala.collection.mutable

/**
 * This stores sequence of Iterators
 *
 * It also provides customization to BufferConfig
 */
abstract class Scheduler(workerCount: Option[Int] = None)(implicit val bufferConfig: BufferConfig = BufferConfig()) extends Operation {

  val workerNum = workerCount

  val config = bufferConfig

  //this keeps a history of iterators
  val opSequence: mutable.Stack[RowIterator] = mutable.Stack()

  def clean(): Unit = opSequence.clear()

}
