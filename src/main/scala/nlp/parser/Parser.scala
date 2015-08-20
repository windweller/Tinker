package nlp.parser

import edu.stanford.nlp.parser.lexparser.LexicalizedParser
import edu.stanford.nlp.trees.Tree

/**
 * Created by anie on 5/23/2015.
 */
class Parser {
  val lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", "-MAX_ITEMS","500000")

  def parse(text: String): Tree = lp.parse(text)
}
