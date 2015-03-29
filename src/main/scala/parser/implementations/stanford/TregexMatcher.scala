package parser.implementations.stanford

import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.DataContainerTypes.{NamedRow, OrdinalRow}
import parser.Parser
import utils.FailureHandle

/**
 * Created by anie on 3/26/2015.
 */
trait TregexMatcher extends Parser with FailureHandle {

  def matches(rowNum: Option[Int] = None, rowStr: Option[String] = None): Unit = {
    if (parsedRules.isEmpty) fatal("Can't proceed if rules aren't specified")
    actionStream += (row => {
      row.left.map(e => matchWithOrdinal(rowNum, e))
      row.right.map(e => matchWithNamed(rowStr, e))
    })
  }

  private[this] def matchWithOrdinal(rowNum: Option[Int], row: OrdinalRow): OrdinalRow = {
    if (rowNum.isEmpty) fatal("Can't proceed if row number is not specified with ordinal rows")
    row ++ matchesFunc(parsedRules.get)(row(rowNum.get))
  }

  private[this] def matchWithNamed(rowStr: Option[String], namedRow: NamedRow): NamedRow = {
    if (namedRow.isEmpty) fatal("Can't proceed if row number is not specified with ordinal rows")
    val mappedRulesWithResult = Map(rules.get.zip(matchesFunc(parsedRules.get)(namedRow(rowStr.get))): _*)
    namedRow ++ mappedRulesWithResult
  }

  //could experience NULL Pointer exception with StanfordNLP Tregex library
  private[this] def matchesFunc(parsedRules: Vector[TregexPattern]): String => Vector[String] =
    (tree: String) =>
      parsedRules.map(r => if (r.matcher(Tree.valueOf(tree)).find()) "1" else "0")

  private[this] def matchesTreeFunc(parsedRules: Vector[TregexPattern]): Tree => Vector[String] =
  	(tree: Tree) => parsedRules.map(r => if (r.matcher(tree).find()) "1" else "0")
}
