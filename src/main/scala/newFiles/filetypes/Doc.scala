package newFiles.filetypes


import newFiles.DataContainer
import newFiles.rowTypes._
import utils._
import utils.OptionToParameter.implicits._

import scala.collection.immutable.HashMap

/**
 * Extends this means that
 * you are working with an Actual file
 * instead of HTTP, Stream, or any other
 * format.
 *
 * It combines old FileTypes and Doc.
 *
 * This is a downstream of DataContainer,
 * because it implements DataContainer's certain methods and types
 */
trait Doc extends DataContainer {

  /**** Abstract methods/variables ****/

  def typesuffix: Vector[String]

  //this method is being overriden by differnt types
  def parse: (String) => Vector[String]

  /**** Concrete implementations ****/

  //get all files
  def files: Map[String, FileIterator] = FileMapIterator.getFileMap(f, header, fuzzyMatch)(typesuffix)

  //allow peaking, assuming all files share same structure
  lazy private[this] val file = files.head._2

  /* this new method forces HashMap on header/nonHeader structure
     only inefficient part is the toString, toInt conversion */
  lazy val headerString: Vector[String] =
    if (file.headerRaw.nonEmpty) parse(file.headerRaw.get)
    else Vector.iterate("0", parse(file.peekHead).length)(pos => (pos.toInt + 1).toString)

  protected def readFileIterator[T](transform: (String) => T, file: FileIterator): Iterator[T] = file.map(l => transform(l))

  def dataIterators: Map[String, RowIterator] =
    files.map(e => e._1 -> readFileIterator[NormalRow]((line) => HashMap(headerString.zip(parse(line)): _*), e._2))

  iterators += dataIterators //add to the mix

}
