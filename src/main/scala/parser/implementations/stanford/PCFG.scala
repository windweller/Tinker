package parser.implementations.stanford

import edu.stanford.nlp.parser.lexparser.LexicalizedParser

/**
 * Created by anie on 4/11/2015.
 */
trait PCFG extends StanfordParser {

  val lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", "-MAX_ITEMS","500000")

}
