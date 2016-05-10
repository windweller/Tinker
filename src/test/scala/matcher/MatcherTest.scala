package matcher

import edu.stanford.nlp.parser.lexparser.LexicalizedParser
import edu.stanford.nlp.trees.tregex.TregexPattern
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ListBuffer

/**
  * Created by aurore on 10/05/16.
  */
class MatcherTest extends FlatSpec with Matchers {
  val matched = ListBuffer[Tuple2[Int, String]]()
  val lp: LexicalizedParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", "-MAX_ITEMS","500000")
  val tree1 = lp.parse("A giant Test")
  val tree2 = lp.parse("test")

  "Rule NN" should "be find" in {
    val matcher = TregexPattern compile("NN") matcher(tree1)
    matcher.find() should be(true)
    val word = matcher.getMatch.children().map(x => x.value()).mkString(" ").trim
    word should be("giant")
  }

  "Rule VBG" should "not be find" in {
    val matcher = TregexPattern compile("VBG") matcher(tree1)
    matcher.find() should be(false)
  }
}
