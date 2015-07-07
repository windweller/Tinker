package processing

import akka.stream.scaladsl.Flow
import files.RowTypes.{NormalRow, RowIterator}
import processing.buffers.BufferConfig

import scala.collection.mutable
import scala.concurrent.Future

/**
 * This stores sequence of Iterators
 *
 * It also provides customization to BufferConfig
 *
 * @param workerCount the default worker count is always 4
 */
abstract class Scheduler(workerCount: Option[Int] = Some(4))(implicit val bufferConfig: BufferConfig = BufferConfig()) extends Operation {

  val workerNum = workerCount

  val config = bufferConfig

  //this keeps a history of iterators
  val opSequence: mutable.Stack[RowIterator] = mutable.Stack()

  def clean(): Unit = opSequence.clear()

  /**
   * add to parallel graph flow
   * @param func pass in this specific function
   */
  def addToGraph(func: NormalRow => Future[NormalRow]): Unit = {
    graphFlows += Flow[NormalRow].mapAsync[NormalRow](func)
  }

}
