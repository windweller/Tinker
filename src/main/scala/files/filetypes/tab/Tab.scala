package files.filetypes.tab

import java.io.{File, RandomAccessFile}
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

import files.Doc
import files.RowTypes._
import processing.buffers.file.{FileOutputFormat, FileBuffer}

import scala.concurrent.Future

/**
 * Created by anie on 4/18/2015
 */
trait Tab extends Doc {

  def typesuffix: Vector[String] = Vector("tab", "txt")

  def parse: (String) => Vector[String] = (line: String) => line.split("\t").toVector

}
