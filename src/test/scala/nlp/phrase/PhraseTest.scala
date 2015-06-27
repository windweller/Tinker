package nlp.phrase

import edu.stanford.nlp.trees.Tree
import org.scalatest.FlatSpec


/**
 * Created by anie on 4/13/2015.
 */
class PhraseTest extends FlatSpec {

//  "Phrase" should "break up sentences into phrases" in {
//    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\NYTimes.tab", true) with Tab with Doc
//    val parser = new Parser(doc) with Phrase
//    val sentence = """(ROOT  (S (NP (PRP I)) (VP (VBD was)   (VP (VBG thinking) (UCP   (PP (IN about)  (NP (PRP$ my) (NN life)(CC and)(NN marriage)))   (CC and)   (SBAR  (SBAR(WHADJP (WRB how) (JJ much))(S  (NP (NP (NN money)   (CC or)   (NN lack)) (PP (IN of)))  (VP (VBZ plays) (NP   (NP (DT a) (NN role))   (PP (IN in) (NP (PRP$ my) (NNS obligations)))))))  (, ,)  (CC and)  (SBAR(WHNP (WP what))(S  (NP (PRP$ my) (NN husband))  (VP (MD would) (VP (VB do)   (SBAR (IN if) (S   (NP (PRP I))   (VP (VBD died)))))))))))) (. .)))"""
//    val result = parser.extractFromSentence(sentence)
//    result.foreach(e => println(e))
//  }
//
//  "Get Leaves" should "reconstruct sentences" in {
//    val sentence = """(ROOT  (S (NP (PRP I)) (VP (VBD was)   (VP (VBG thinking) (UCP   (PP (IN about)  (NP (PRP$ my) (NN life)(CC and)(NN marriage)))   (CC and)   (SBAR  (SBAR(WHADJP (WRB how) (JJ much))(S  (NP (NP (NN money)   (CC or)   (NN lack)) (PP (IN of)))  (VP (VBZ plays) (NP   (NP (DT a) (NN role))   (PP (IN in) (NP (PRP$ my) (NNS obligations)))))))  (, ,)  (CC and)  (SBAR(WHNP (WP what))(S  (NP (PRP$ my) (NN husband))  (VP (MD would) (VP (VB do)   (SBAR (IN if) (S   (NP (PRP I))   (VP (VBD died)))))))))))) (. .)))"""
//    val treeListIt = Tree.valueOf(sentence).getLeaves.iterator()
//    var result = ""
//    while (treeListIt.hasNext) {
//      result += treeListIt.next()
//      result += " "
//    }
//    println(result)

//  }

}
