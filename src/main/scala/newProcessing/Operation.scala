package newProcessing

import newFiles.RowTypes.RowIterator
import newProcessing.buffers.{BufferConfig, Buffer}

import scala.collection.mutable.ArrayBuffer

/**
 * All modules such as parser
 */
trait Operation extends Buffer {

  val opSequence: ArrayBuffer[RowIterator]

  def exec(): Unit
  def save(): Unit = exec()

}