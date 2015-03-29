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

  def parseline(line: String): Vector[String] = {

    var result: Vector[String] = Vector.empty[String]

    Try(CSVParser.parse(line, csvFormat)) match {
      case Success(parser) =>
        val parserIt = parser.iterator()
        while (parserIt.hasNext) {
          val record = parserIt.next()
          result = collectRow(Vector.empty[String], record.iterator())
        }
        result
      case Failure(ex) =>
        fatal(ex.getMessage)
        throw ex
    }
  }

  @tailrec
  private[this] def collectRow(row: Vector[String], rowStringIt: Iterator[String]): Vector[String] = {
    if (rowStringIt.hasNext) collectRow(row :+ rowStringIt.next, rowStringIt)
    else row
  }
}

object CSVHandler {

  def apply(): CSVHandler = new CSVHandler(CSVFormat.RFC4180) //default
  def apply(csvFormat: CSVFormat): CSVHandler = new CSVHandler(csvFormat) //custom

  private lazy val defaultCSVHandler = apply()

  def parseline(line: String): Vector[String] = defaultCSVHandler.parseline(line)
}
