package tutorial

import files.DataContainer
import files.filetypes.input.CSV
import files.operations.FileOp
import files.structure.{DataStruct, DataStructure}
import parser.implementations.StanfordNLP.EnglishPCFGParser
import utils.ParameterCallToOption.Implicits._
import files.structure.Index._

/**
 * Created by anie on 7/14/2015
 */
object Parser extends App {
  val data = new DataContainer("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\test1.csv", header = true) with CSV with EnglishPCFGParser with FileOp

  data.parse(None, DataStruct(targetColumnWithName = "Sentence"))
  data.drop(DataStruct(targetRange = 4 to last))

  data.save("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\testResult.csv")
}
