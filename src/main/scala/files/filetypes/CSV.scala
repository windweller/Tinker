package files.filetypes

import java.io.RandomAccessFile
import java.nio.file.Path

import com.bizo.mighty.csv.CSVWriter
import files.DataContainerTypes._
import files.parsers.CSVHandler
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Provide save and parse function
 */
trait CSV extends FileTypes {

  override val typesuffix: Vector[String] = Vector("csv")

  abstract override def save(data: Vector[Vector[String]], outputFile: String): Future[Unit] = {
   Future{ val output: CSVWriter = CSVWriter(outputFile)
    data.foreach(d => output.write(d))}
  }

  abstract override def parse: (String) => Vector[String] = CSVHandler.parseline

  import com.github.tototoshi.csv.CSVWriter

  override def save(it: NormalRow)(implicit file: Option[Path]): Unit = {
    if (file.isEmpty) fatal("You haven't included module FileBuffer")
    val output: CSVWriter = CSVWriter.open(file.get.toString, append = true)
    if (file.get.toFile.length() == 0 && it.isRight) printWithHeaderKeys(it.right.get, output)
    else {
      it.left.foreach(row => output.writeRow(row))
      it.right.foreach(row => output.writeRow(row.values.toSeq))
    }
    output.close()
  }

  private[this] def printWithHeaderKeys(row: NamedRow, output: CSVWriter): Unit = {
    output.writeRow(row.keys.toSeq)
    output.writeRow(row.values.toSeq)
  }

  override def printHeader(it: NormalRow)(implicit file: Option[Path]): Unit = {
    if (file.isEmpty) fatal("You haven't included module FileBuffer")
    if (it.isLeft) fatal("Cannot print header if the original file has no header")
    val output: CSVWriter = CSVWriter.open(file.get.toString, append = true)
    output.writeRow(it.right.get.keys.toSeq)
    output.close()
  }

  override def printHeader(it: Option[Vector[String]])(implicit file: Option[Path]): Unit = {
    if (it.isEmpty) fatal("Cannot print header if original file has no header")
    val output: CSVWriter = CSVWriter.open(file.get.toString, append = true)
    output.writeRow(it.get)
    output.close()
  }

}