package nlp.future

import edu.stanford.nlp.parser.lexparser.LexicalizedParser
import edu.stanford.nlp.trees.tregex.TregexPattern
import org.scalatest.FlatSpec

/**
 * Created by Aimingnie on 7/3/15
 * tracking the progression/sequence of future rules being triggered
 * through the sentence
 */
class Progression extends FlatSpec {
  behavior of "progression of rule tracking"

  it should "track and generate sequence" in {
    val lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", "-MAX_ITEMS","500000")
    val tree = lp.parse("We are about to do this")
//    val rule = TregexPattern.compile("S < ( VP < TO < ( VP < VB ))")
    val rule = TregexPattern.compile("S < VP")
    val result = rule.matcher(tree)
    println(tree.toString)
    println(result.find())
    //Method 1: use dominationPath
    println(tree.dominationPath(result.getMatch).size())
//    println(tree.localTrees())

  }

}