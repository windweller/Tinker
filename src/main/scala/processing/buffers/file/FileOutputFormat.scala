package processing.buffers.file

import files.RowTypes._
import files.structure.DataStructure
import utils.FailureHandle

/**
 * Created by Aimingnie on 5/17/15.
 */
trait FileOutputFormat extends FailureHandle {

  //will get and encode header, Array(0) is key, Array(1) is value
  def encodeHeader(row: NormalRow, struct: Option[DataStructure]): Array[String] = {fatal("must implement an actual Output Module"); Array()}

  def encode(row: NormalRow, struct: Option[DataStructure]): String = {fatal("must implement an actual Output Module"); ""}

  def outputSuffix: String = {fatal("must implement an actual Output Module"); ""}

}
