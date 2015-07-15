package tutorial

import files.DataContainer
import files.filetypes.input.CSV
import files.structure.DataStructure
import parser.implementations.StanfordNLP
import parser.implementations.StanfordNLP.EnglishPCFGParser
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 7/14/2015.
 */
object Parser extends App {
  val data = new DataContainer("./src/test/scala/tutorial/data/csvFile.csv", header = true) with CSV with EnglishPCFGParser
  data.parse(None, parser.Struct(idColumn = 5, targetColumn = 3))

}
