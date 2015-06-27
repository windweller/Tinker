package processing.buffers.file

import files.RowTypes._

/**
 * Created by Aimingnie on 5/17/15.
 */
trait FileOutputFormat {

  //will get and encode header, Array(0) is key, Array(1) is value
  def encodeHeader(row: NormalRow): Array[String]

  def encode(row: NormalRow): String

  def outputSuffix: String

}
