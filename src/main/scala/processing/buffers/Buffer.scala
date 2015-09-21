package processing.buffers

import core.TypedRow
import core.structure.DataSelect
import utils.FailureHandle

/**
 * Created by anie on 4/19/2015
 */
trait Buffer {

  var config: BufferConfig

  //this is to shield away the saving method
  //no matter saving to a file, database, or something else
  def bufferWrite(row: TypedRow, select: Option[DataSelect], ignore: Option[DataSelect]): Unit

  //Close the connection to outputStream
  //whether it's file, database, or something else
  def bufferClose(): Unit

}

//this is an all encompassing Config case class
case class BufferConfig(var filePath: Option[String] = None,
                         var fileAppend: Boolean = true,
                         var fileEncoding: String = "UTF-8",
                         var fileWithHeader: Boolean = true) extends FailureHandle {
  def checkFileBuffer(): Unit = {
    if (filePath.isEmpty) fail("Buffer Config doesn't contain filePath field")
  }
}
