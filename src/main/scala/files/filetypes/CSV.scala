package files.filetypes

import com.bizo.mighty.csv.{CSVDictReader, CSVReader, CSVWriter}
import files.Doc
import files.parsers.CSVHandler
import org.apache.commons.csv.CSVFormat
import scala.collection.mutable.ArrayBuffer

/**
 * Provide a whole-file read access, and an iterator access
 */
class CSV(f: String, header: Boolean, rawData: Option[ArrayBuffer[Array[String]]], CSVformat: Option[CSVFormat]) extends Doc {

  lazy val data = if (rawData.isEmpty) processCSV(f, header) else rawData.get

  def save(loc: String): Unit = {
    val output: CSVWriter = CSVWriter(loc)
    data.foreach(r => output.write(r))
  }

  def processCSV(loc: String, header: Boolean): ArrayBuffer[Array[String]] = {
    readFile(loc, header, (line) =>  CSVHandler.parseline(line))
  }

  //what's the return type of this?
  def iterator = if (header) Right(CSVDictReader(f)) else Left(CSVReader(f))

  override def parse: (String) => Seq[String] = CSVHandler.parseline
}

object CSV {
  //access file this way if you want to read file
  def apply(f: String, header: Boolean): CSV = new CSV(f, header, None, None)
  def apply(f: String, header: Boolean, data: ArrayBuffer[Array[String]]): CSV = new CSV(f, header, Some(data), None)
  def apply(f: String, header: Boolean, data: ArrayBuffer[Array[String]], format: CSVFormat): CSV = new CSV(f, header, Some(data), Some(format))

}
