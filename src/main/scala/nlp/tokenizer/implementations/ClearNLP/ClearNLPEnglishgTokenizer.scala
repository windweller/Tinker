package nlp.tokenizer.implementations.ClearNLP

import core.DataContainer
import core.structure.DataSelect
import edu.emory.clir.clearnlp.component.utils.NLPUtils
import edu.emory.clir.clearnlp.util.lang.TLanguage
import nlp.tokenizer.Tokenizer

/**
 * Created by anie on 7/28/2015.
 */
trait ClearNLPEnglishgTokenizer extends Tokenizer {

  this: DataContainer =>

  val tokenizer = NLPUtils.getTokenizer(TLanguage.ENGLISH)

  override def tokenize(struct: DataSelect): DataContainer with Tokenizer = {
    //nlp.tokenizer.tokenize(struct.getTargetValue(row).get)

    this
  }

}
