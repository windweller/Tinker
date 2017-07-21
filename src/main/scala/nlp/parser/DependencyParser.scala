package nlp.parser

import edu.stanford.nlp.parser.nndep.DependencyParser
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.simple._


class DependencyParser {
  def parse(text: String) {
    val sent = new Sentence(text)
    return sent.incomingDependencyLabels()
  }
}
