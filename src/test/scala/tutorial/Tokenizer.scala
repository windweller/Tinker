package tutorial

import core.DataContainer
import core.structure.DataSelect
import files.filetypes.input.Tab
import nlp.tokenizer.implementations.Stanford.StanfordPTBTokenizer
import utils.ParameterCallToOption.Implicits._

/**
 * Created by Aimingnie on 10/3/15.
 */
object Tokenizer extends App{

  val data = new DataContainer("./src/test/scala/tutorial/data/sentences_untokenized.tab",
                  header = true,
                  core = 4) with Tab with StanfordPTBTokenizer

  data.tokenize(select = DataSelect(targetColumnWithName = "Sentence")).toTab

  data.save("./src/test/scala/tutorial/data/sentences_tokenized.tab")

}
