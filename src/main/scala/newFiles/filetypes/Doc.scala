package newFiles.filetypes


import newFiles.DataContainer
import newFiles.rowTypes._
import utils.FailureHandle
import utils.OptionToParameter.implicits._

import scala.collection.parallel.immutable.ParVector

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
trait Doc extends DataContainer with FailureHandle {

  /**** Abstract methods/variables ****/

  def typesuffix: Vector[String]

  //a fresh iterator every time
  def file = FileIterator(f, header)(typesuffix)

  //this method is being overriden by differnt types
  def parse: (String) => Vector[String]

  /**** Concrete implementations ****/

  lazy val headerString: Option[ParVector[String]] = file.headerRaw.map(header => parse(header).par)

  protected def readFileIterator[T](transform: (String) => T): Iterator[T] = file.map(l => transform(l))

  def dataIterator: RowIterator = {
    readFileIterator[NormalRow]((line) => {
      if (header) headerString.get.map(e => Some(e)).zip(parse(line))
      else parse(line).par.map(e => (None, e))
    })
  }

  iterators += dataIterator //add to the mix

  //you need to do benchmark to know if par collection would work better than seq collection
  //it is necessary, because NLP columns have more than 1 million columns sometime


}
