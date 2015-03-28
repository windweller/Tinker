package parser.implementations.stanford

import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import parser.Parser

/**
 * Created by anie on 3/26/2015.
 */
trait TregexMatcher extends Parser {

  def matches(rules: Vector[TregexPattern]): Unit = {
    actionStream += (data => new Parser(data, false))
  }

  //could experience NULL Pointer exception
  private[this] def matchesFunc(rules: Vector[TregexPattern]): String => Vector[Int]   =
    (tree: String) =>
      rules.map(r => if (r.matcher(Tree.valueOf(tree)).find()) 1 else 0)

  private[this] def matchesTreeFunc(rules: Vector[TregexPattern]): Tree => Vector[Int] =
  	(tree: Tree) => rules.map(r => if (r.matcher(tree).find()) 1 else 0)
}
