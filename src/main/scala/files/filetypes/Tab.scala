package files.filetypes


import java.nio.file.Path

import files.DataContainer
import files.DataContainerTypes._
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
  abstract override def save(data: Vector[Vector[String]], outputFile: String): Future[Unit] =
    Future {
      val fc = new RandomAccessFile(new File(outputFile), "rw").getChannel
      val bufferSize= data.length
      val mem: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize)
      data.foreach(e => mem.put(e.mkString("\t").getBytes))
      mem.force()
      fc.close()
    }

  //header printing is done in another method
  override def save(it: NormalRow)(implicit file: Path): Future[Unit] = Future {
    val rafile = new RandomAccessFile(file.toFile, "rw")
    it.left.foreach(row => rafile.write(row.mkString("\t").getBytes))
    it.right.foreach(row => rafile.write(row.values.mkString("\t").getBytes))
  }

  override def printHeader(it: NormalRow)(implicit file: Path): Unit = {
    if (it.isLeft) fatal("Cannot print header if the original file has no header")
    val rafile = new RandomAccessFile(file.toFile, "rw")
    rafile.write(it.right.get.keys.mkString("\t").getBytes)
  }


}
