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

  abstract override def save(data: Vector[Array[String]], outputFile: String): Future[Unit] = {
   Future{ val output: CSVWriter = CSVWriter(outputFile)
    data.foreach(d => output.write(d))}
  }

  abstract override def parse: (String) => Array[String] = CSVHandler.parseline

}

