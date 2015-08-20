package experiments.emnlp2015

import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import nlp.parser.Parser
import org.scalatest.FlatSpec

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 8/4/2015.
 */
class PreWord2VecFutureTest extends FlatSpec {

  "new tregex scheme" should "split out a sentence with rules inserted" in {
    val parser = new Parser
    val patterns = Array("VBD", "VBG").map(e => TregexPattern.compile(e))
    val tree = parser.parse("I was going to eat an apple.")

//    patterns.foreach { pattern =>
//
//      val matcher = pattern.matcher(tree)
//      if (matcher.find()) {
//        //we should go to the tree node
//        val matchedNode = matcher.getMatch
//        matchedNode.setValue("$R5" + " " + matchedNode.value())
//        println(matchedNode)
//      }
//    }

    val result = ArrayBuffer.empty[String]
    traverseTree(tree, patterns, result)
    println(result.mkString(" "))

  }

    private def traverseTree(tree: Tree, patterns: Array[TregexPattern], blocks: ArrayBuffer[String]): Unit = {

      if (tree != null) {
        val children = tree.children()
        val it = children.iterator

        while (it.hasNext) {
          val currentTree = it.next()
          var added = false
           //start pattern matching
          (0 to patterns.length - 1).foreach { i =>
            val matcher = patterns(i).matcher(currentTree)
            if (matcher.findAt(currentTree)) {
              blocks += "$R"+ i + " " + currentTree.value()
              added = true
            }
          }
          if (!added) blocks += currentTree.value()
          traverseTree(currentTree, patterns, blocks)
        }
      }
    }

}
