package tutorial

import files.DataContainer
import files.filetypes.input._
import files.operations.FileOp
import files.structure.DataSelect
import matcher.implementations.TregexMatcher
import parser.implementations.StanfordNLP.EnglishPCFGParser
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 7/14/2015
 */
object Parser extends App {
  val data = new DataContainer("./src/test/scala/tutorial/data/sentences.tab",
                                header = true,
                                core = 10) with Tab with EnglishPCFGParser with TregexMatcher with FileOp

  data.parse(None, DataSelect(targetColumnWithName = "Sentence"))

  data.matcher(patternsRaw = List(
    "(VP < (VBG < going) < (S < (VP < TO)))",
    "(VP < (VBG < going) > (PP < TO))",
    "MD < will"
  ), struct = DataSelect()).toTab

  data.save("./src/test/scala/tutorial/data/sentences_parsed.tab")
}
