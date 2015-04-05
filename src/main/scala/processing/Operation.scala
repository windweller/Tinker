package processing

import files.{Doc, DataContainer}
import utils.FailureHandle
import buffers.Buffer

import scala.collection.mutable.ArrayBuffer
import files.DataContainerTypes._

/**
 * Created by anie on 4/1/2015.
 */
trait Operation extends Buffer {

  import OperationType._

  val dataContainer: DataContainer
  val actionStream: ArrayBuffer[(IntermediateResult) => IntermediateResult] = ArrayBuffer.empty[(IntermediateResult) => IntermediateResult]

}

object OperationType extends FailureHandle {
  type GeneratedRow = NormalRow
  type IntermediateResult = (NormalRow, Option[GeneratedRow])

  def combine(a: NormalRow, b: GeneratedRow): NormalRow = {
    if (a.isLeft && b.isLeft)
      Left(a.left.get ++ b.left.get)
    else if (a.isRight && b.isRight)
      Right(a.right.get ++ b.right.get)
    else {fatal("Row must be either array based or mapped row based."); throw new Exception}
  }

  def combine(a: NormalRow, b: Option[GeneratedRow]): NormalRow = {
    if (b.isEmpty) a else combine(a, b.get)
  }
}
