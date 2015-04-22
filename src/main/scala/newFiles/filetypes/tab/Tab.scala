package newFiles.filetypes.tab

import java.io.{File, RandomAccessFile}
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

import newFiles.filetypes.Doc

import scala.concurrent.Future

/**
 * Created by anie on 4/18/2015
 */
trait Tab extends Doc {

  override val typesuffix: Vector[String] = Vector("tab", "txt")

  override def parse: (String) => Vector[String] = (line: String) => line.split("\t").toVector

}
