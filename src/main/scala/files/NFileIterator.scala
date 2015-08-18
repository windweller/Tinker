package files

import java.io.File

import utils.FailureHandle

import scala.annotation.tailrec

/**
 * Created by anie on 8/17/2015
 *
 * Will store header inside.
 * This pre-filter and generate an array of files that guarantees
 * to be full
 *
 * It takes care of codecs (for most of them)
 * and removes non-ASCII characters, so it takes longer time than simple traversal
 */
class NFileIterator(preFiles: Array[File], val header: Boolean) extends Iterator[String] with FailureHandle {

  //generate list of non-empty files
  val files = preFiles.filter(file => file.length() != 0L)

  private[this] val defaultCodecs = Vector(
    scala.io.Codec("ISO-8859-1"),
    scala.io.Codec("UTF-8"),
    scala.io.Codec("UTF-16"),
    scala.io.Codec("Cp1252"),
    scala.io.Codec("Cp037"),
    scala.io.Codec("Cp1149")
  )

  var headerString = ""

  private[this] val remainingFiles = files.iterator
  private[this] var currentFile = remainingFiles.next()
  private[this] var currentFileIt = getIterator(currentFile, defaultCodecs.iterator)

  override def hasNext: Boolean = remainingFiles.hasNext || currentFileIt.hasNext

  override def next(): String = {
    //if the current one is already empty
    if (!currentFileIt.hasNext) {
      currentFile = remainingFiles.next()
      currentFileIt = getIterator(currentFile, defaultCodecs.iterator)
      if (header) checkHeader(currentFileIt.next())
    }
    currentFileIt.next().replaceAll("[^\\x00-\\x7F]", "")
  }

  /** ==== Utility Methods ==== **/

  //check if header is consistent, also fill up header when it's empty
  private[this] def checkHeader(header: String): Unit = {
    if (headerString == "") headerString = header.replaceAll("[^\\x00-\\x7F]", "")
    else if (headerString != header.replaceAll("[^\\x00-\\x7F]", "")) {
      println("previous header: " + headerString)
      println("current header: " + header)
      fatal("file: " + currentFile.getName + " does not have matching header")
    }
  }

  //fixed the encoding problem
  @tailrec
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
}
