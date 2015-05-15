package newFiles.filetypes.csv

import com.github.tototoshi.csv._
import newFiles.RowTypes._
import newFiles.filetypes.Doc
import newProcessing.buffers.FileBuffer

/**
 * Created by anie on 4/18/2015
 *
 * The CSV encoding was adopted from scala-csv by tototoshi
 * because the method is not available
 */
trait CSV extends Doc with FileBuffer {

  def encodeWithHeader(row: NormalRow): Array[String]

  def encode(row: NormalRow): String =
    generateString(row.valuesIterator.toSeq)

  def outputSuffix: String = "csv"

  def typesuffix = Vector("csv", "txt")

  def parse: (String) => Vector[String] = CSVHandler.parseline

  implicit object MyFormat extends DefaultCSVFormat
  val format = MyFormat


  private[this] def generateString(fields: Seq[Any]): String = {

    def shouldQuote(field: String, quoting: Quoting): Boolean =
      quoting match {
        case QUOTE_ALL => true
        case QUOTE_MINIMAL =>
          List("\r", "\n", format.quoteChar.toString, format.delimiter.toString).exists(field.contains)
        case QUOTE_NONE => false
        case QUOTE_NONNUMERIC =>
          if (field.forall(_.isDigit)) {
            false
          } else {
            val firstCharIsDigit = field.headOption.exists(_.isDigit)
            if (firstCharIsDigit && (field.filterNot(_.isDigit) == ".")) {
              false
            } else {
              true
            }
          }
      }

    def quote(field: String): String =
      if (shouldQuote(field, format.quoting)) field.mkString(format.quoteChar.toString, "", format.quoteChar.toString)
      else field

    def repeatQuoteChar(field: String): String =
      field.replace(format.quoteChar.toString, format.quoteChar.toString * 2)

    def escapeDelimiterChar(field: String): String =
      field.replace(format.delimiter.toString, format.escapeChar.toString + format.delimiter.toString)

    def show(s: Any): String = Option(s).getOrElse("").toString

    val renderField = {
      val escape = format.quoting match {
        case QUOTE_NONE => escapeDelimiterChar _
        case _ => repeatQuoteChar _
      }
      quote _ compose escape compose show
    }

    fields.map(renderField).mkString(format.delimiter.toString)
  }


}
