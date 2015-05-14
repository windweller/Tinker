package newFiles.filetypes.tab

import java.io.{File, RandomAccessFile}
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

import newFiles.RowTypes._
import newFiles.filetypes.Doc
import newProcessing.buffers.FileBuffer

import scala.concurrent.Future

/**
 * Created by anie on 4/18/2015
 */
trait Tab extends Doc with FileBuffer {

  def encode(row: NormalRow): String = {

  }

  def outputSuffix: String = "tab"

  def typesuffix: Vector[String] = Vector("tab", "txt")

  def parse: (String) => Vector[String] = (line: String) => line.split("\t").toVector

}
