package files.parsers

import org.apache.commons.csv.{CSVRecord, CSVParser, CSVFormat}
import utils.FailureHandle
import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

/**
 * Created by anie on 3/20/2015.
 */

class CSVHandler(csvFormat: CSVFormat) extends FailureHandle {

  def parseline(line: String): Array[String] = {

    var result: Array[String] = Array()

    Try(CSVParser.parse(line, csvFormat)) match {
      case Success(parser) =>
        val parserIt = parser.iterator()
        while (parserIt.hasNext) {
          val record = parserIt.next()
          result = collectRow(Array(), record.iterator())
        }
        result
      case Failure(ex) =>
        fatal(ex.getMessage)
        throw ex
    }
  }

  @tailrec
  private[this] def collectRow(row: Array[String], rowStringIt: Iterator[String]): Array[String] = {
    if (rowStringIt.hasNext) collectRow(row :+ rowStringIt.next, rowStringIt)
    else row
  }
}

object CSVHandler {

  def apply(): CSVHandler = new CSVHandler(CSVFormat.RFC4180) //default
  def apply(csvFormat: CSVFormat): CSVHandler = new CSVHandler(csvFormat) //custom

  private val defaultCSVHandler = apply()

  def parseline(line: String): Array[String] = defaultCSVHandler.parseline(line)
}
