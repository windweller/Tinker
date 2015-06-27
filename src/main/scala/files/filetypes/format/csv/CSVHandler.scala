package files.filetypes.format.csv

import org.apache.commons.csv.{CSVRecord, CSVFormat, CSVParser}
import utils.FailureHandle

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

/**
 * Created by anie on 3/20/2015
 */

class CSVHandler(csvFormat: CSVFormat) extends FailureHandle {

  /**
   * added error handling, will not parse the line if malformed
   * this is almost like an emergency patch. Should be fixed the next update
   * @param line
   * @return
   */
  def parseline(line: String): Vector[String] = {

    var result: Vector[String] = Vector.empty[String]

    Try(CSVParser.parse(line, csvFormat)) match {
      case Success(parser) =>
        val parserIt = parser.iterator()
        Try(parserIt.hasNext).foreach { b =>
          if (b) {
            val record = parserIt.next()
            result = collectRow(Vector.empty[String], record.iterator())
          }
        }
        result
      case Failure(ex) =>
        fatal(ex.getMessage)
        ex.printStackTrace()
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
