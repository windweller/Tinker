package files

import java.io.File

/**
 * Best way to traverse a large file
 * or files
 * This is basically a directory iterator to help Akka Stream
 */
class FileIterator(files: Array[File], val header: Boolean) extends Iterator[String] {

  private[this] val remainingFiles = files.iterator
  private[this] var currentFile = remainingFiles.next()
  private[this] var currentFileIterator = getIterator(currentFile)

  override def next(): String = {
    if (currentFileIterator.hasNext)
      currentFileIterator.next()
    else {
      currentFile = remainingFiles.next()
      currentFileIterator = getIterator(currentFile)
      next()
    }
  }

  private[this] def getIterator(file: File): Iterator[String] = {
    val it = io.Source.fromFile(currentFile).getLines()
    if (header) { //skip header
      if (it.hasNext) it.next()
      it
    }
    else it
  }

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
  def apply(folder: String, header: Boolean): FileIterator = {
    val dir = new File(folder)
    if (dir.isDirectory) new FileIterator(dir.listFiles(), header) else new FileIterator(Array(dir), header)
  }
}
