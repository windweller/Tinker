package newProcessing

import newFiles.RowTypes.{RowIterator, NormalRow}
import newProcessing.buffers.BufferConfig

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * This stores sequence
 */
abstract class Scheduler(workerCount: Int)(implicit val bufferConfig: BufferConfig) extends Operation {

  val config = bufferConfig

  //this keeps a history of iterators
  val opSequence: ArrayBuffer[RowIterator] = ArrayBuffer.empty[RowIterator]

  def clean(): Unit = opSequence.clear()

}
