package files.filetypes

import com.bizo.mighty.csv.CSVWriter
import files.DataContainer
import files.parsers.CSVHandler
import scala.collection.mutable.ArrayBuffer
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

}

