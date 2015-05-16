package newProcessing

import newFiles.RowTypes.RowIterator
import newProcessing.buffers.{BufferConfig, Buffer}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * All modules such as parser
 */
trait Operation extends Buffer {

  val workerNum: Int  //for parallel only

  val opSequence:  mutable.Stack[RowIterator]

  def exec(): Unit
  def save(): Unit = exec()

}