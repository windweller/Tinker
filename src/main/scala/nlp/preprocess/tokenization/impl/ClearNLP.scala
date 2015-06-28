package nlp.preprocess.tokenization.impl

import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer
import nlp.preprocess.tokenization.Tokenizer
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 5/22/2015
 *
 * Not usable
 */
trait ClearNLP extends Tokenizer {

  val tokenizer = new EnglishTokenizer()

  //could cause problem
  override def tokenize(): Tokenizer = {

    new Tokenizer(data, struct)(scheduler) with ClearNLP
  }
}
