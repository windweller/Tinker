package nlp.matcher.impl

import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import nlp.matcher.Matcher

/**
 * Created by anie on 4/26/2015.
 */
trait Tregex extends Matcher {

  def search(tree: Tree, patterns: List[TregexPattern]): Array[Int] = {
    val stats =  Array.fill[Int](patterns.size)(0)

    patterns.indices.foreach { i =>

      try {
        val matcher = patterns(i).matcher(tree)
        if (matcher.find()) {
          stats(i) = stats(i) + 1
        }
      } catch {
        case e: NullPointerException =>
          //this happens when a tree is malformed
          //we will not add any number to stats, just return it as is
          println("NULL Pointer with " + tree.toString)
      }
    }
    stats
  }

  def trackProgress(tree: Tree, patterns: List[TregexPattern]): Unit = {
    patterns.indices.foreach { i =>
      val matcher = patterns(i).matcher(tree)
      matcher.getMatch
    }
  }

}
