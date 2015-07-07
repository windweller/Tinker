package parser.implementations.stanfordNLP

import parser.Parser

/**
 * Created by Aimingnie on 5/12/15.
 */
trait Tregex extends Parser {
  /**
   * all the method edits scheduler directly (past records are kept)
   * however they do return a string indicating the changed/added column's name
   *
   * @param columnName Enter the name that you want the newly generated column to be
   *
   * @return the generated column name (columnName if it's specified)
   */
  override def parse(columnName: Option[String]): String = {
    ""
  }
}
