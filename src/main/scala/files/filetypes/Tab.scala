package files.filetypes


import files.DataContainer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import java.io.File
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Created by anie on 3/19/2015.
 * (f: String, header: Boolean, rawData: Option[ArrayBuffer[Array[String]]])
 */
trait Tab extends DataContainer {

  override val typesuffix: Vector[String] = Vector("tab", "txt")

  abstract override def parse: (String) => Vector[String] = (line: String) => line.split("\t").toVector

  //Implements Java NIO MemoryMapper
  //forcefully writing to disk after data is done
  abstract override def save(data: Vector[Vector[String]], outputFile: String): Future[Unit] = {
    Future {
      val fc = new RandomAccessFile(new File(outputFile), "rw").getChannel
      val bufferSize= data.length
      val mem: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize)
      data.foreach(e => mem.put(e.mkString("\t").getBytes))
      mem.force()
    }
  }

}
