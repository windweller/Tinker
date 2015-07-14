package tutorial

import files.DataContainer
import files.filetypes.input.CSV
import parser.implementations.stanfordNLP.EnglishPCFG
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 7/14/2015.
 */
object Parser extends App {
  val data = new DataContainer("./src/test/scala/tutorial/data/csvFile.csv", header = true) with CSV
}
