package newProcessing.buffers

import java.nio.file.{Files, Paths}

import newFiles.RowTypes.NormalRow
import utils.Global

import scala.util.Random

/**
 * Created by anie on 4/19/2015
 */
trait FileBuffer extends Buffer {

  def encodeWithHeader(row: NormalRow): Array[String]

  def encode(row: NormalRow): String

  def outputSuffix: String

  //this is to shield away the saving method
  override def bufferSave()(implicit config: BufferConfig): Unit = {
    config.filePath match {
      case Some(file) =>

      case None =>
        val tempFile = Paths.get(System.getProperty("user.home")).resolve(".Tinker")
        if (Files.notExists(tempFile)) Files.createDirectory(tempFile)
        val random = Random.nextInt().toString
        val name = random + tempFile + ".csv"
        Files.createTempFile(tempFile, random, ".csv").toFile.deleteOnExit()
        Global.tempFiles.push(name)
        //not done, actually save stuff!

    }

  }

  private[this] def saveToFile(): Unit = {

  }


}
