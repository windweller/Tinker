package processing

import java.nio.file.Path

import files.filetypes.FileTypes
import files.{Doc, DataContainer}
import utils.FailureHandle
import buffers.Buffer

import scala.collection.mutable.ArrayBuffer
import files.DataContainerTypes._

/**
 * Created by anie on 4/1/2015.
 */
trait Operation extends FileTypes with Buffer {

  import OperationType._

  val data: DataContainer with Doc
  val actionStream: ArrayBuffer[(IntermediateResult) => IntermediateResult] = ArrayBuffer.empty[(IntermediateResult) => IntermediateResult]

}

object OperationType extends FailureHandle {
  type GeneratedRow = NormalRow
  type IntermediateResult = (NormalRow, Option[GeneratedRow])

  //let's hope the flatMap is working
  def combine(a: NormalRow, b: GeneratedRow): NormalRow = {
    if (a.isLeft && b.isLeft)
      a.left.flatMap(a => b.left.map(b => a ++ b))
    else if (a.isRight && b.isRight)
      a.right.flatMap(a => b.right.map(b => a ++ b))
    else {fatal("Row must be either array based or mapped row based."); throw new Exception}
  }

  def combine(a: NormalRow, b: Option[GeneratedRow]): NormalRow = {
    if (b.isEmpty) a else combine(a, b)
  }
}
