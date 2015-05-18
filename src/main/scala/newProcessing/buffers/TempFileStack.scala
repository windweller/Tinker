package newProcessing.buffers

import scala.collection.mutable

/**
 * Created by anie on 5/16/2015
 *
 * To overcome default stack's inefficiency
 */
class TempFileStack {

  //for fast append operation
  val tempFiles: mutable.ListBuffer[String] = mutable.ListBuffer.empty[String]
  private[this] var lastFile: String = ""

  def push(str: String): Unit = {
    tempFiles.append(str)
    lastFile = str
  }

  def pop(): String = lastFile

}
