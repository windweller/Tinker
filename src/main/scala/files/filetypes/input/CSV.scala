package files.filetypes.input

import files.Doc
import files.filetypes.input.csv.CSVHandler

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
