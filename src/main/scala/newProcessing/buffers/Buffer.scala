package newProcessing.buffers

import utils.FailureHandle

/**
 * Created by anie on 4/19/2015.
 */
trait Buffer {

  //this is to shield away the saving method
  //no matter saving to a file, database, or something else
  def bufferSave()(implicit config: BufferConfig): Unit

}

//this is an all encompassing Config case class
case class BufferConfig(filePath: Option[String] = None,
                        fileOverride: Option[Boolean] = None) extends FailureHandle {
  def checkFileBuffer(): Unit = {
    if (filePath.isEmpty) fail("Buffer Config doesn't contain filePath field")
  }
}
