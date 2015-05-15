package newProcessing

import newFiles.RowTypes.RowIterator
import newProcessing.buffers.{BufferConfig, Buffer}

import scala.collection.mutable.ArrayBuffer

/**
 * All modules such as parser
 */
trait Operation extends Buffer {

  val opSequence: ArrayBuffer[RowIterator]

  def exec()(implicit config: Option[BufferConfig] = None): Unit
  def save(config: BufferConfig): Unit = exec()(Some(config))

}