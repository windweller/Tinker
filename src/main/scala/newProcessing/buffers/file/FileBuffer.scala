package newProcessing.buffers.file

import java.io.{OutputStreamWriter, FileOutputStream, PrintWriter}
import java.nio.file.{Files, Paths}

import newFiles.RowTypes.NormalRow
import newProcessing.buffers.Buffer
import utils.{FailureHandle, Global}

import scala.util.Random

/**
 * Created by anie on 4/19/2015
 *
 * Don't know if the writing efficiency is high enough
 * The module under Operation would close this
 */
trait FileBuffer extends Buffer with FailureHandle {

  lazy val printer: PrintWriter = new PrintWriter(new OutputStreamWriter(
                                                        new FileOutputStream(config.filePath.get,
                                                          config.fileAppend),
                                                          config.fileEncoding))

  var headerPrinted = false

  //will get and encode header, Array(0) is key, Array(1) is value
  def encodeHeader(row: NormalRow): Array[String]

  def encode(row: NormalRow): String

  def outputSuffix: String

  //this is to shield away the saving method
  def bufferWrite(row: NormalRow): Unit = {

    if (config.filePath.isEmpty) createTempFile()

    if (config.fileWithHeader && !headerPrinted) {
      val arr = encodeHeader(row)
      printer.write(arr(0))
      printer.write(arr(1))
      headerPrinted = true
    }
    else {
      printer.write(encode(row))
    }
  }

  def bufferClose(): Unit = {
    printer.flush()
    printer.close()
  }

  //this method creates a temporary file and push to stack
  private[this] def createTempFile(): Unit = {

    val tempFile = Paths.get(System.getProperty("user.home")).resolve(".Tinker")
    if (Files.notExists(tempFile)) Files.createDirectory(tempFile)

    val random = Random.nextInt().toString
    val name = tempFile + random + ".csv"
    Files.createTempFile(tempFile, random, ".csv").toFile.deleteOnExit()

    Global.tempFiles.push(name)
    config.filePath = Some(name)

  }

}
