package parser

import files.DataContainer
import files.structure.DataStruct

/**
 * Created by anie on 7/16/2015.
 */
trait Matcher {
  def matcher(newColumn: Option[String] = None, struct: DataStruct): DataContainer with Parser

}
