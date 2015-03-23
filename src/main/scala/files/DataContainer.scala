package files

import files.filetypes.FileTypes
import org.apache.commons.csv.CSVFormat
import utils.FailureHandle

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

/**
 * This is the BASE class for the whole
 * stackable trait pattern
 *
 * It is not perfect (too many visible/redefining methods),
 * it will be refactored for 1.0 release
 *
 * We can extend base class with other traits. The functions
 * that are destined to be override, must have concrete implementations
 * in those traits
 *
 * @param rawData this is the data to be saved put in by user
 */
abstract class DataContainer(val f: String, val header: Boolean,
                             val rawData: Option[Vector[Array[String]]], val CSVformat: Option[CSVFormat])
                              extends FailureHandle with FileTypes {

  val data: Vector[Array[String]]

  val file: FileIterator

  lazy val headerMap: Option[Array[String]] = file.headerRaw.map(header => parse(header))

  //For a file with header, we sometimes just want to ignore them
  def dataIteratorPure: Iterator[Array[String]] = {
    fatal("Cannot obtain iterator without mixing in Doc trait")
    throw new Exception
  }

  def dataIterator: Either[Iterator[Array[String]], Iterator[Map[String, String]]] = {
    fatal("Cannot obtain iterator without mixing in Doc trait")
    throw new Exception
  }

  //Auxiliary Constructor (Sacrifice made for Stackable Trait Pattern)
  //You have to always invoke "new" to aseembly the desired class
  def this(f: String, header: Boolean) {this(f, header, None, None)}
  def this(f: String, header: Boolean, data: Vector[Array[String]]) {this(f, header, Some(data), None)}
  def this(f: String, header: Boolean, data: Vector[Array[String]], format: CSVFormat) {this(f, header, Some(data), Some(format))}
}
