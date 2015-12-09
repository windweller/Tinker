package tutorial

import core.DataContainer
import core.structure.DataSelect
import files.filetypes.input.Tab
import nlp.matcher.implementations.TregexMatcher
import nlp.parser.implementations.StanfordNLP.EnglishPCFGParser
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 7/14/2015
 */
object Parser extends App {
  val data = new DataContainer("./src/test/scala/tutorial/data/sentences.tab",
                                header = true,
                                core = 4) with Tab with EnglishPCFGParser with TregexMatcher

  data.parse(None, DataSelect(targetColumnWithName = "Sentence"))

  data.matcher(patternsRaw = List(
    "(VP < (VBG < going) < (S < (VP < TO)))",
    "(VP < (VBG < going) > (PP < TO))",
    "MD < will"
  )).toTab

  data.save("./src/test/scala/tutorial/data/sentences_parsed.tab")

}