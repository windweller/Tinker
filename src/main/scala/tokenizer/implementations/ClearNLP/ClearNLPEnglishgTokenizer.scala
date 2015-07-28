package tokenizer.implementations.ClearNLP

import edu.emory.clir.clearnlp.component.utils.NLPUtils
import edu.emory.clir.clearnlp.util.lang.TLanguage
import files.DataContainer
import files.structure.DataStruct
import tokenizer.Tokenizer

import scala.concurrent.Future

/**
 * Created by anie on 7/28/2015.
 */
trait ClearNLPEnglishgTokenizer extends Tokenizer {

  this: DataContainer =>

  val tokenizer = NLPUtils.getTokenizer(TLanguage.ENGLISH)

  override def tokenize(struct: DataStruct): DataContainer with Tokenizer = {
    //tokenizer.tokenize(struct.getTargetValue(row).get)

    this
  }

}
