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

  override def save(it: NormalRow)(implicit file: Option[Path]): Future[Unit] = Future {
    if (file.isEmpty) fatal("You haven't included module FileBuffer")
    val output: CSVWriter = CSVWriter(file.get.toString)
    it.left.foreach(row => output.write(row))
    it.right.foreach(row => output.write(row.values.toSeq)) //this might be slower, and could be order of order? Check
  }

  override def printHeader(it: NormalRow)(implicit file: Option[Path]): Unit = {
    if (file.isEmpty) fatal("You haven't included module FileBuffer")
    if (it.isLeft) fatal("Cannot print header if the original file has no header")
    val output: CSVWriter = CSVWriter(file.get.toString)
    output.write(it.right.get.keys.toArray)
  }

}