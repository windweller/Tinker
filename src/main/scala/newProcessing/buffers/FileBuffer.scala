package newProcessing.buffers

import java.nio.file.{Files, Paths}

/**
 * Created by anie on 4/19/2015.
 */
trait FileBuffer extends Buffer {
  //this is to shield away the saving method
  override def bufferSave()(implicit config: BufferConfig): Unit = {
    config.filePath match {
      case Some(file) =>

      case None =>
        val tempFile = Paths.get(System.getProperty("user.home")).resolve(".Tinker")
        if (Files.notExists(tempFile)) Files.createDirectory(tempFile)
        Files.createTempFile(tempFile, "parser", ".csv").toFile.deleteOnExit()
        //not done, actually save stuff!
    }

  }
}
