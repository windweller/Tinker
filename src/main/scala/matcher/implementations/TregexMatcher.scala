package matcher.implementations

import application.Application
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.DataContainer
import files.structure.DataSelect
import matcher.Matcher
import utils.{FailureHandle, Timer}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Aimingnie on 5/12/15
 */
trait TregexMatcher extends Matcher with FailureHandle {

  this: DataContainer =>

  override def matcher(file: Option[String] = None,
                       patternsRaw: Option[List[String]] = None,
                       struct: DataSelect = DataSelect()): DataContainer with Matcher = {

    if (patternsRaw.isEmpty && file.isEmpty) {
      fail("you must put in file or list of patterns")
    } else {
      val patternsText = rulesFromFile(file).getOrElse(patternsRaw.get)
      val patterns = patternsText.map(e =>  TregexPattern.compile(e))
      scheduler.addToGraph(row => scala.concurrent.Future {
        if(Application.verbose) Timer.completeOne
        val tree = Tree.valueOf(struct.getTargetValue(row).getOrElse(row("parsed")))
        val array = search(tree, patterns).map(i => i.toString)
        row ++= mutable.HashMap(patternsText.zip(array): _*)
        row
      })
    }
    this
  }

  private def search(tree: Tree, patterns: List[TregexPattern]): Array[Int] = {
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
