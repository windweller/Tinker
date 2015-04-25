package newFiles.filetypes

import java.io.File

import utils.FailureHandle

/**
 * Best way to traverse a large file
 * or files
 * This is basically a directory iterator to help Akka Stream
 *
 * This treats all the files as the same
 */
class FileIterator(files: Array[File], val header: Boolean) extends Iterator[String] with FailureHandle {

  private[this] val remainingFiles = files.iterator
  private[this] var currentFile = remainingFiles.next()
  private[this] var currentFileIterator = getIterator(currentFile)
  val headerRaw = getHeader //this is the header of the first file
  val currentFileName = currentFile.getName

  def getHeader: Option[String] =
    if (header && currentFileIterator.hasNext) Some(currentFileIterator.next()) else None

  def headerCheck(): Unit = {
    val result = if (header && currentFileIterator.hasNext) Some(currentFileIterator.next()) else None
    result.map(e => e == headerRaw.get).foreach(e => if (!e) fatal("file: " + currentFile.getName + " does not have matching header"))
  }

  override def next(): String = {
    if (currentFileIterator.hasNext)
      currentFileIterator.next()
    else {
      currentFile = remainingFiles.next()
      currentFileIterator = getIterator(currentFile)
      headerCheck()
      next()
    }
  }

  private[this] def getIterator(file: File): Iterator[String] = io.Source.fromFile(file).getLines()

  override def hasNext: Boolean = remainingFiles.hasNext || currentFileIterator.hasNext

}

object FileIterator {
  /**
   * WARNING: all files must have the same format, including header,
   * if one file has header, the other doesn't, this set up will not work
   * @param folder
   * @param header
   * @return
   */
  def apply(folder: String, header: Boolean)(suffix: Vector[String]): FileIterator = {
    val dir = new File(folder)
    val files = if (dir.isDirectory) dir.listFiles().filter(f => !f.isDirectory).filter(f => suffix.contains(f.getName.split("\\.")(1)))
                else Array(dir)
    if (dir.isDirectory) new FileIterator(files, header) else new FileIterator(Array(dir), header)
  }
}
