package tutorial

import files.DataContainer
import files.filetypes.input.CSV
import files.structure.DataSelect
import matcher.implementations.FutureTregexMatcher
import parser.implementations.StanfordNLP.EnglishPCFGParser
import searcher.implementations.TreeSearcher
import utils.ParameterCallToOption.Implicits._

/**
 * Created by aurore
 */
object Parser4 extends App {
  val data = new DataContainer("/home/aurore/Documents/CNRS/Corpus Splitté/xaa.tab",
    header = true, core = 4) with CSV with EnglishPCFGParser with FutureTregexMatcher with TreeSearcher

  data.parse(None, DataSelect(targetColumnWithName = "Tweet"))

  data.matcher(patternsRaw = List(
    "S <+ (!S) (NP < (DT < (this .. TN|TC)))",
    "S <+ (!S) (PP < (IN < in|for) < (NP <, DT|CD|JJ < TC|TN))",
    "S <+ (!S) (PP < (IN < at) < (NP < CD))",
    "S <+ (!S) (PP  < (IN < on|for) << TN)",
    "S <+ (!S) (RB < later)",
    "S <+ (!S) (ADVP < TN|TC < (RB < away))",
    "S <+ (!S) (VP < TN|TC < (ADVP < (RB < away)))",
    "S <+ (!S) (TN < today)",
    "S <+ (!S) (until|'til|today|now)",
    "S <+ (!S) (TN|TC)" //règle très large : contient now et today
  ), struct = DataSelect()).toTab

  data.search(patternsRaw = List(
    "JJ|VBN > (ADJP > (VP<(VB|VBD|VBG|VBN|VBP|VBZ < be|am|'m|is|’s|are|’re|was|were|been) &> (S|SQ (!>>S|SQ))))"
  ), struct = DataSelect()).toTab

  data.save("./src/test/scala/tutorial/data/answer.tab")
}