package parser.implementations.stanford

import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import parser.Parser
import parser.implementations.ParserImpl

/**
 * Created by anie on 3/26/2015.
 */
trait TregexMatcher extends ParserImpl{

  //could experience NULL Pointer exception
  def matches(rules: Vector[TregexPattern]): String => Vector[Int]   =
    (tree: String) =>
      rules.map(r => if (r.matcher(Tree.valueOf(tree)).find()) 1 else 0)

  def matchesTree(rules: Vector[TregexPattern]): Tree => Vector[Int] =
  	(tree: Tree) => rules.map(r => if (r.matcher(tree).find()) 1 else 0)
}
