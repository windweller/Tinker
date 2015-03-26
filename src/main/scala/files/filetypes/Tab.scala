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

  abstract override def parse: (String) => Array[String] = (line: String) => line.split("\t")

  //Implements Java NIO MemoryMapper
  //forcefully writing to disk after data is done
  abstract override def save(data: Vector[Array[String]], outputFile: String): Future[Unit] = {
    Future {
      val fc = new RandomAccessFile(new File(outputFile), "rw").getChannel
      val bufferSize= data.length
      val mem: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize)
      data.foreach(e => mem.put(e.mkString("\t").getBytes))
      mem.force()
    }
  }

}
