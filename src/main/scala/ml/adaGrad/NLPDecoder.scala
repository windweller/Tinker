package ml.adaGrad

import edu.emory.clir.clearnlp.component.AbstractComponent
import edu.emory.clir.clearnlp.component.utils.NLPUtils
import edu.emory.clir.clearnlp.util.lang.TLanguage

import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 7/27/2015.
 */
class NLPDecoder(language: TLanguage) {

  val tokenizer = NLPUtils.getTokenizer(language)

//  def getGeneralModels(language: TLanguage): ArrayBuffer[AbstractComponent] = {
//    //initialize statistical models
//    //TODO: I don't think this is the right way to load jar model
//    val pos = NLPUtils.getPOSTagger(language, "general-en-pos.xz")
//
//
//  }
}
