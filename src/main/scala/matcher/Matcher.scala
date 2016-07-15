package matcher

import files.DataContainer
import files.structure.DataSelect

/**
 * Created by anie on 7/16/2015
 * A matcher is to search a certain structure/string
 */
trait Matcher {
  def matcher(newColumn: Option[String] = None,
              patterns: Option[List[String]] = None,
              struct: DataSelect): DataContainer with Matcher
}
