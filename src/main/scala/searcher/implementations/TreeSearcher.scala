package searcher.implementations

import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.DataContainer
import files.structure.DataSelect
import searcher.Searcher
import utils.FailureHandle

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by aurore on 15/12/15.
 */
trait TreeSearcher extends Searcher with FailureHandle {
  this: DataContainer =>

  override def search(file: Option[String] = None,
                       patternsRaw: Option[List[String]] = None,
                       struct: DataSelect = DataSelect()): DataContainer with Searcher = {

    if (patternsRaw.isEmpty && file.isEmpty) {
      fail("you must put in file or list of patterns")
    } else {
      val patternsText = rulesFromFile(file).getOrElse(patternsRaw.get)
      val patterns = patternsText.map(e =>  TregexPattern.compile(e))
      scheduler.addToGraph(row => scala.concurrent.Future {
        val tree = Tree.valueOf(struct.getTargetValue(row).getOrElse(row("parsed")))
        val array = getSearch(tree, patterns).map(i => i)
        row ++= mutable.HashMap(patternsText.zip(array): _*)
        row
      })
    }
    this
  }

  private def getSearch(tree: Tree, patterns: List[TregexPattern]): Array[String] = {
    val answers =  Array.fill[String](patterns.size)("")

    patterns.indices.foreach { i =>

      try {
        val matcher = patterns(i).matcher(tree)
        if (matcher.find()) {
          answers(i) = matcher.getMatch.toString()
        }
      } catch {
        case e: NullPointerException =>
          //this happens when a tree is malformed
          //we will not add any number to stats, just return it as is
          println("NULL Pointer with " + tree.toString)
      }
    }
    answers
  }

  private def rulesFromFile(fileLoc: Option[String]): Option[List[String]] =
    fileLoc.map(file => scala.io.Source.fromFile(file).getLines().toList)
}

