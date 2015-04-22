package parser.phrase

import edu.stanford.nlp.trees.Tree
import files.DataContainerTypes._
import parser.Parser
import utils.FailureHandle
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 4/11/2015.
 * For phrase extraction
 *
 * right now it's non-parallelable
 */
trait Phrase extends Parser with FailureHandle {

  //dataContainer

  def extract(rowNum: Option[Int] = None, rowStr: Option[String] = None): Unit = {
    val it = dataContainer.dataIterator
    while (it.nonEmpty) {
      val row = it.next()
      val clauses = ArrayBuffer.empty[String]
      row.left.foreach(row => traverseTree(Tree.valueOf(row(rowNum.get)), clauses))
      row.right.foreach(row => traverseTree(Tree.valueOf(row(rowStr.get)), clauses))
    }
  }

  def extractFromSentence(sentence: String): ArrayBuffer[String] ={
    val clauses = ArrayBuffer.empty[String]
    traverseTree(Tree.valueOf(sentence), clauses)
    clauses += clause
  }

  var clause = ""
  private def traverseTree(tree: Tree, clauses: ArrayBuffer[String]): Unit = {

    if (tree != null) {

      //reach bottom
      if (tree.isLeaf) {
        clause += tree.value() + " "
      }
      else {
        val children = tree.children()
        val it = children.iterator
        while (it.hasNext) {
          val currentTree = it.next()
          if (currentTree.value() == "S") {
            clauses += clause
            clause = ""
          }
          traverseTree(currentTree, clauses)
        }
      }
    }
  }
}
