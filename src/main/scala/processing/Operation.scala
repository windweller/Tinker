package processing

import akka.stream.scaladsl.Flow
import core.TypedRow
import core.structure.{DataSelect, DataStruct}
import processing.buffers.Buffer
import utils.FailureHandle

import scala.collection.mutable

/**
 * All modules such as nlp.parser
 */
trait Operation extends Buffer with FailureHandle {

  var dataStructure: DataStruct

  var graphFlows: mutable.ListBuffer[Flow[TypedRow, TypedRow, Unit]] =
                                    mutable.ListBuffer.empty[Flow[TypedRow, TypedRow, Unit]]

  val workerNum: Option[Int]  //for parallel only

  var opSequence: mutable.Stack[Iterator[TypedRow]]

  def exec(select: Option[DataSelect] = None, ignore: Option[DataSelect] = None): Unit

}