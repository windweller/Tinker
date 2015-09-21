package files.filetypes.output

import com.github.tototoshi.csv._
import core.TypedRow
import core.structure.DataSelect
import processing.buffers.file.FileOutputFormat

/**
 * Created by anie on 5/18/2015
 *
 * This trait has to be created because
 * Scala enforces trait inhericance making Scheduler
 * inherit DataContainer (which is illogical)
 */
trait CSVOutput extends FileOutputFormat {

  implicit object MyFormat extends DefaultCSVFormat
  val format = MyFormat

  /**
   *
   * @param row
   * @param select with ignore, could be both empty
   * @param ignore
   * @return
   */
  override def encodeHeader(row: TypedRow, select: Option[DataSelect] = None, ignore: Option[DataSelect] = None): Array[String] = {

    val cleanedRow = row.select(select, ignore)

    Array(generateString(cleanedRow.getOrElse(row).keysIterator.toSeq),
                    generateString(cleanedRow.getOrElse(row).valuesIterator.toSeq))
  }

  override def encode(row: TypedRow, select: Option[DataSelect] = None, ignore: Option[DataSelect] = None): String = {
    val cleanedRow = row.select(select, ignore)
    generateString(cleanedRow.getOrElse(row).valuesIterator.toSeq)
  }


  override def outputSuffix: String = "csv"

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
