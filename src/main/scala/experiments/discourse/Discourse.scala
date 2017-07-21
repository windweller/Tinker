package experiments

import files.DataContainer
import files.filetypes.input.{CSV, Tab}
import files.structure.DataStructure
import nlp.future.FutureRules._
import nlp.preprocess.tokenization.Tokenizer
import nlp.preprocess.tokenization.impl.Stanford
import utils.ParameterCallToOption.Implicits._

import edu.stanford.nlp.simple._

object Discourse extends App {

  val sent = new Sentence("Lucy is in the sky with diamonds.")
  println(sent.incomingDependencyLabels())

}
