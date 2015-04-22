package newFiles.filetypes.csv

import newFiles.filetypes.Doc

/**
 * Created by anie on 4/18/2015
 */
trait CSV extends Doc {

  override val typesuffix = Vector("csv")

  override def parse: (String) => Vector[String] = CSVHandler.parseline

}
