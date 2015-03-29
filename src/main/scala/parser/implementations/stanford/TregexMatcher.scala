package parser.implementations.stanford

import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import parser.Parser
import utils.FailureHandle

/**
 * Created by anie on 3/26/2015.
 */
trait TregexMatcher extends Parser with FailureHandle {

  def matches(rowNum: Option[Int] = None, rowStr: Option[String] = None): Unit = {
    if (parsedRules.isEmpty) fatal("Can't proceed if rules aren't specified")

    actionStream += (row => {
      row match {
        case Left(ordinalRow) =>
          if (rowNum.isEmpty) fatal("Can't proceed if row number is not specified with ordinal rows")
          Left(row.left ++ matchesFunc(parsedRules.get)(ordinalRow(rowNum.get)))
        case Right(namedRow) =>
          if (rowStr.isEmpty) fatal("Can't proceed if row header name is not specified with named rows")
          row.joinRight(matchesFunc(parsedRules.get)(namedRow(rowStr.get)))
      }
    })
  }

  //could experience NULL Pointer exception with StanfordNLP Tregex library
  private[this] def matchesFunc(rules: Vector[TregexPattern]): String => Vector[Int]   =
    (tree: String) =>
      rules.map(r => if (r.matcher(Tree.valueOf(tree)).find()) 1 else 0)

  private[this] def matchesTreeFunc(rules: Vector[TregexPattern]): Tree => Vector[Int] =
  	(tree: Tree) => rules.map(r => if (r.matcher(tree).find()) 1 else 0)
}
