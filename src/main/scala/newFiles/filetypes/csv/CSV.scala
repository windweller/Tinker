package newFiles.filetypes.csv

import newFiles.filetypes.Doc

/**
 * Created by anie on 4/18/2015
 */
trait CSV extends Doc {

  def typesuffix = Vector("csv", "txt")

  def parse: (String) => Vector[String] = CSVHandler.parseline

}
