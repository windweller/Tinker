package files

import java.io.File

import utils.FailureHandle

import scala.annotation.tailrec
import scala.util.Try

/**
 * Best way to traverse a large file
 * or files
 * This is basically a directory iterator to help Akka Stream
 *
 * This treats all the files as the same
 */
class FileIterator(files: Array[File], val header: Boolean) extends Iterator[String] with FailureHandle {

  private[this] val defaultCodecs = Vector(
    scala.io.Codec("ISO-8859-1"),
    scala.io.Codec("UTF-8"),
    scala.io.Codec("UTF-16"),
    scala.io.Codec("Cp1252"),
    scala.io.Codec("Cp037"),
    scala.io.Codec("Cp1149")
  )

  private[this] var remainingFiles = files.iterator
  private[this] var currentFile = remainingFiles.next()
  private[this] var currentFileIterator = getIterator(currentFile, defaultCodecs.iterator)
  val headerRaw = getHeader //this is the header of the first file

  lazy val firstRow = if (header && currentFileIterator.hasNext) {
    next()
    val row = next() //skip header and grab the first row
    reset()
    row
  }
  else peekHead

  //this is a costly. To get the string of the first row
  lazy val peekHead: String = {
    val peek = next()
    reset()
    peek
  }

  private[this] def reset(): Unit = {
    //reset everything
    remainingFiles = files.iterator
    currentFile = remainingFiles.next()
    currentFileIterator = getIterator(currentFile, defaultCodecs.iterator)
  }

  def getHeader: Option[String] = if (header && currentFileIterator.hasNext) Some(currentFileIterator.next()) else None

  //this checks header consistency across files
  private[this] def headerCheck(): Unit = {
    val result = if (header && currentFileIterator.hasNext) Some(currentFileIterator.next()) else None
    result.map(e => e == headerRaw.get).foreach(e => if (!e) fatal("file: " + currentFile.getName + " does not have matching header"))
  }

  //this has a strong assumption that the next
  //file is guaranteed not to be empty
  override def next(): String = {
    println(currentFile.getName)
    currentFileIterator.next()
  }

  import scala._
  //fixed the encoding problem
  private[this] def getIterator(file: File, codecs:Iterator[io.Codec]): Iterator[String] = {
    if (codecs.hasNext)
      try {
        val codec = codecs.next()
        val it = io.Source.fromFile(file)(codec).getLines()
        it.next()
        io.Source.fromFile(file)(codec).getLines()
      }
      catch {
        case ex: Exception =>
          getIterator(file, codecs)
      }
    else
      io.Source.fromFile(file).getLines()  //all custom codecs have failed, switching to default
  }

  override def hasNext: Boolean = checkEmpty(remainingFiles.hasNext || currentFileIterator.hasNext)

  //this function fast forward and skip through empty files
  @tailrec
  private[this] def checkEmpty(status: Boolean): Boolean = {
    //at any level it's false, we return false
    //if true, we examine further, but normally this is true (because
    //remaining file is almost always true)
    if (!status)
      false
    else {
      if (currentFileIterator.hasNext)
        true
      else if (remainingFiles.hasNext) {
        currentFile = remainingFiles.next()
        currentFileIterator = getIterator(currentFile, defaultCodecs.iterator)
        headerCheck()
        if (currentFileIterator.hasNext) true
        else checkEmpty(remainingFiles.hasNext)
      }
      else false
    }
  }

}

object FileIterator {
  /**
   * WARNING: all files must have the same format, including header,
   * if one file has header, the other doesn't, this set up will not work
   */
  def apply(folder: String, header: Boolean)(suffix: Vector[String]): FileIterator = {
    val dir = new File(folder)
    val files = if (dir.isDirectory) dir.listFiles().filter(f => !f.isDirectory).filter(f => suffix.contains(f.getName.split("\\.")(1)))
                else Array(dir)
    if (dir.isDirectory) new FileIterator(files, header) else new FileIterator(Array(dir), header)
  }
}
