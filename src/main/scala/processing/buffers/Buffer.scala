package processing.buffers

import files.RowTypes.NormalRow
import utils.FailureHandle

/**
 * Created by anie on 4/19/2015
 */
trait Buffer {

  val config: BufferConfig

  //this is to shield away the saving method
  //no matter saving to a file, database, or something else
  def bufferWrite(row: NormalRow): Unit

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
