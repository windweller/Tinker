package files.filetypes.format

<<<<<<< HEAD:src/main/scala/files/filetypes/csv/CSV.scala
import com.github.tototoshi.csv._
import files.Doc
import files.RowTypes._
import processing.buffers.file.FileOutputFormat
=======
import files.filetypes.Doc
import files.filetypes.format.csv.CSVHandler
>>>>>>> origin/master:src/main/scala/files/filetypes/format/CSV.scala

/**
 * Created by anie on 4/18/2015
 *
 * The CSV encoding was adopted from scala-csv by tototoshi
 * because the method is not available
 */
trait CSV extends Doc {

  def typesuffix = Vector("csv", "txt")

  def parse: (String) => Vector[String] = CSVHandler.parseline

}
