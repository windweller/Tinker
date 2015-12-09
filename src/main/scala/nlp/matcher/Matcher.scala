package nlp.matcher

import core.DataContainer
import core.structure.DataSelect

/**
 * Created by anie on 7/16/2015
 * A nlp.matcher is to search a certain structure/string
 */
trait Matcher {

  def matcher(newColumn: Option[String] = None,
              patterns: Option[List[String]] = None,
              select: DataSelect,
              constructTree: Boolean = false): DataContainer with Matcher
}