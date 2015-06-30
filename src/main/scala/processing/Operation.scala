package processing

import files.RowTypes.RowIterator
import processing.buffers.Buffer

import scala.collection.mutable

/**
 * All modules such as parser
 */
trait Operation extends Buffer {

  val workerNum: Option[Int]  //for parallel only

  val opSequence: mutable.Stack[RowIterator]

  def exec(): Unit
  def save(): Unit = exec()

}