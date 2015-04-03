package files

import files.DataContainerTypes._
import files.filetypes.FileTypes
import org.apache.commons.csv.CSVFormat
import processing.Operation
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
                             val rawData: Option[Vector[Vector[String]]], val CSVformat: Option[CSVFormat])
                              extends FileTypes with FailureHandle with Operation {

  val data: Vector[Vector[String]]

  //For a file with header, we sometimes just want to ignore them
  def dataIteratorPure: Iterator[OrdinalRow] = {
    fatal("Cannot obtain iterator without mixing in Doc trait")
    throw new Exception
  }

  def dataIterator: RowIterator = {
    fatal("Cannot obtain iterator without mixing in Doc trait")
    throw new Exception
  }

  def save(): Unit = save(data, f)
  def save(output: String): Unit = save(data, output)

  //Auxiliary Constructor (Sacrifice made for Stackable Trait Pattern)
  //You have to always invoke "new" to aseembly the desired class
  def this(f: String) {this(f, false, None, None)}
  def this(f: String, header: Boolean) {this(f, header, None, None)}
  def this(f: String, header: Boolean, format: CSVFormat) {this(f, header, None, Some(format))}
  def this(f: String, data:Vector[Vector[String]]) {this(f, false, Some(data), None)} //for saving, must attach file type
  def this(f: String, data:Vector[Vector[String]], format: CSVFormat) {this(f, false, Some(data), Some(format))} //for saving, must attach file type
}

object DataContainerTypes {
  type OrdinalRow = Vector[String]
  type NamedRow = Map[String, String]
  type NormalRow= Either[OrdinalRow, NamedRow]
  type RowIterator = Iterator[Either[OrdinalRow, NamedRow]]
  type RowSelector = Either[Int, String]
}