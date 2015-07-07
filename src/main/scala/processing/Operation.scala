package processing

import akka.stream.scaladsl.Flow
import files.RowTypes.{NormalRow, RowIterator}
import files.structure.DataStructure
import processing.buffers.Buffer

import scala.collection.mutable

/**
 * All modules such as parser
 */
trait Operation extends Buffer {

  val graphFlows: mutable.ListBuffer[Flow[NormalRow, NormalRow, Unit]] =
                                    mutable.ListBuffer.empty[Flow[NormalRow, NormalRow, Unit]]

  val workerNum: Option[Int]  //for parallel only

  val opSequence: mutable.Stack[RowIterator]

  def exec(struct: Option[DataStructure] = None): Unit

}