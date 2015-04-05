package files.filetypes

import java.io.{File, RandomAccessFile}
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path

import files.DataContainer
import files.DataContainerTypes.NormalRow
import baseModules.Output
import files.structure.DataStructure
import utils.FailureHandle

import scala.concurrent.Future

/**
 * FileTypes include normal/generic file types
 * as well as special file types such as
 * MalletFileType or VarroSubtreeXML format
 *
 * FileTypes trait is not just about file type
 * it's mostly about saving/reading strategy
 */
trait FileTypes extends Output with FailureHandle{

  val typesuffix: Vector[String] = Vector.empty[String]

  val headerString: Option[Vector[String]]
  val headerMap: Option[Map[String, Int]]

  protected def parse: (String) => Vector[String] = (line: String) => Vector(line)

  protected def parseWithHeader: (String) => Map[String, String] = {
    (nextLine: String) => {
      if (headerString.nonEmpty)
        Map(headerString.get.zip(parse(nextLine)): _*)
      else {fatal("Cannot invoke header parse for a file with no header"); throw new Exception}
    }
  }

  //save function is rather independent of DataContainer
  //put in correct address, and you are done
  def save(data: Vector[Vector[String]], outputFile: String): Future[Unit] = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }

  def save(row: Vector[String])(implicit file: Option[Path], dt: Option[DataStructure] = None): Unit = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }

  //this is to save iteratively
  override def save(it: NormalRow)(implicit file: Option[Path]): Unit = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }

  def printHeader(it: NormalRow)(implicit file: Option[Path]): Unit = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }

  def printHeader(it: Option[Vector[String]])(implicit file: Option[Path]): Unit = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }

}
