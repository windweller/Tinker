package processing.buffers.file

import java.io.{FileOutputStream, OutputStreamWriter, PrintWriter}
import java.nio.file.{Files, Paths}

import core.TypedRow
import core.structure.DataSelect
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
  def bufferWrite(row: TypedRow, select: Option[DataSelect], ignore: Option[DataSelect]): Unit = {

    if (config.filePath.isEmpty) createTempFile()

    if (config.fileWithHeader && !headerPrinted) {
      val arr = encodeHeader(row, select, ignore)
      printer.println(arr(0))
      printer.println(arr(1))
      headerPrinted = true
    }
    else {
      printer.println(encode(row, select, ignore))
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
