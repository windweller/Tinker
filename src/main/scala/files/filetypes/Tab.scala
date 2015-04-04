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
trait Tab extends FileTypes {

  override val typesuffix: Vector[String] = Vector("tab", "txt")

  abstract override def parse: (String) => Vector[String] = (line: String) => line.split("\t").toVector

  //Implements Java NIO MemoryMapper
  //forcefully writing to disk after data is done
  override def save(data: Vector[Vector[String]], outputFile: String): Future[Unit] = Future {
      val fc = new RandomAccessFile(new File(outputFile), "rw").getChannel
      val bufferSize= data.length
      val mem: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize)
      data.foreach(e => mem.put(e.mkString("\t").getBytes))
      mem.force()
      fc.close()
    }

  override def save(it: NormalRow)(implicit file: Option[Path]): Unit = {
    if (file.isEmpty) fatal("You haven't included module FileBuffer")
    val f = file.get.toFile
    val rafile = new RandomAccessFile(f, "rw")
    rafile.seek(f.length())
    if (f.length() == 0 && it.isRight) printWithHeaderKeys(it.right.get, rafile)
    it.left.foreach(row => rafile.write(row.mkString("\t").concat("\r\n").getBytes))
    it.right.foreach(row => rafile.write(row.values.mkString("\t").concat("\r\n").getBytes))
    rafile.close()
  }

  private[this] def printWithHeaderKeys(row: NamedRow, output: RandomAccessFile): Unit = {
    output.write(row.keys.mkString("\t").concat("\r\n").getBytes)
    output.write(row.values.mkString("\t").concat("\r\n").getBytes)
  }

  override def printHeader(it: NormalRow)(implicit file: Option[Path]): Unit = {
    if (file.isEmpty) fatal("You haven't included module FileBuffer")
    if (it.isLeft) fatal("Cannot print header if the original file has no header")
    val rafile = new RandomAccessFile(file.get.toFile, "rw")
    rafile.write(it.right.get.keys.mkString("\t").concat("\r\n").getBytes)
    rafile.close()
  }

  override def printHeader(it: Option[Vector[String]])(implicit file: Option[Path]): Unit = {
    if (it.isEmpty) fatal("Cannot print header if original file has no header")
    val rafile = new RandomAccessFile(file.get.toFile, "rw")
    rafile.write(it.get.mkString("\t").concat("\r\n").getBytes)
    rafile.close()
  }


}
