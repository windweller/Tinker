package nlp.parser.impl

import edu.stanford.nlp.parser.lexparser.LexicalizedParser
import edu.stanford.nlp.trees.Tree
import nlp.parser.Parser

/**
 * Created by anie on 4/26/2015.
 */
trait StanfordPCFG extends Parser {

  val lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", "-MAX_ITEMS","500000")

  //assumption is sentences were split before
  def parse(text: String): Tree = {
    lp.parse(text)
  }

}
