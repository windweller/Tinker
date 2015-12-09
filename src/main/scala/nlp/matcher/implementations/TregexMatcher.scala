package nlp.matcher.implementations

import core.DataContainer
import core.structure.DataSelect
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import nlp.matcher.Matcher
import utils.FailureHandle

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Aimingnie on 5/12/15
 */
trait TregexMatcher extends Matcher with FailureHandle {

  this: DataContainer =>

  override def matcher(file: Option[String] = None,
                       patternsRaw: Option[List[String]] = None,
                       select: DataSelect = DataSelect(),
                       constructTree: Boolean = false): DataContainer with Matcher = {

    if (patternsRaw.isEmpty && file.isEmpty) {
      fail("you must put in file or list of patterns")
    } else {

      //should be efficient and only calculated once
      val patternsText = rulesFromFile(file).getOrElse(patternsRaw.get)
      val patterns = patternsText.map(e =>  TregexPattern.compile(e))

      scheduler.addToGraph(row => scala.concurrent.Future {
        val tree = if (!constructTree) row.get[Tree](select.target.getOrElse("parsed")).get
                   else Tree.valueOf(row.get[String](select.target.get).get)
        val array = search(tree, patterns).map(i => i.toString)
        array.indices.foreach{ i =>
          row addValuePair(patternsText(i) -> array(i), 'Int)
        }
        row
      })
    }
    this
  }



  private[this] def search(tree: Tree, patterns: List[TregexPattern]): Array[Int] = {
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

  private def rulesFromFile(fileLoc: Option[String]): Option[List[String]] =
                  fileLoc.map(file => scala.io.Source.fromFile(file).getLines().toList)

}
