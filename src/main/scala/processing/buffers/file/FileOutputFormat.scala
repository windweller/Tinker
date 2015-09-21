package processing.buffers.file

import core.TypedRow
import core.structure.DataSelect

/**
 * Created by Aimingnie on 5/17/15.
 */
trait FileOutputFormat {

  //will get and encode header, Array(0) is key, Array(1) is value
  def encodeHeader(row: TypedRow, select: Option[DataSelect] = None, ignore: Option[DataSelect] = None): Array[String]

  def encode(row: TypedRow, select: Option[DataSelect] = None, ignore: Option[DataSelect] = None): String

  def outputSuffix: String

}
