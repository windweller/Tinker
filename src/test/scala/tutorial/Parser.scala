package tutorial

import files.DataContainer
import files.filetypes.input.CSV
import files.operations.FileOp
import files.structure.{DataStruct, DataStructure}
import parser.implementations.StanfordNLP.{TregexMatcher, EnglishPCFGParser}
import utils.ParameterCallToOption.Implicits._
import files.structure.Index._

/**
 * Created by anie on 7/14/2015
 */
object Parser extends App {
  val data = new DataContainer("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\test1.csv",
                                header = true,
                                core = 15) with CSV with EnglishPCFGParser with TregexMatcher with FileOp

  data.parse(None, DataStruct(targetColumnWithName = "Sentence"))
      .matcher(file = "E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\Rules2_2_2.txt", struct = DataStruct())

  data.save("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\testResult.csv")
}
