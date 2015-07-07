package processing.buffers.file

import java.io.{OutputStreamWriter, FileOutputStream, PrintWriter}
import java.nio.file.{Files, Paths}

import files.RowTypes.NormalRow
import files.structure.DataStructure
import processing.buffers.Buffer
import utils.{FailureHandle, Global}

import scala.util.Random

/**
 * Created by anie on 4/19/2015
 *
 * Don't know if the writing efficiency is high enough
 * The module under Operation would close this
 */
trait FileBuffer extends Buffer with FileOutputFormat with FailureHandle {

  lazy val printer: PrintWriter = new PrintWriter(new OutputStreamWriter(
                                                        new FileOutputStream(config.filePath.get,
                                                          config.fileAppend),
                                                          config.fileEncoding))

  var headerPrinted = false

  //this is to shield away the saving method
  def bufferWrite(row: NormalRow, struct: Option[DataStructure]): Unit = {

    if (config.filePath.isEmpty) createTempFile()

    if (config.fileWithHeader && !headerPrinted) {
      val arr = encodeHeader(row, struct)
      printer.println(arr(0))
      printer.println(arr(1))
      headerPrinted = true
    }
    else {
      printer.println(encode(row, struct))
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
