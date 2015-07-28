package parser


import files.DataContainer
import files.structure.{DataStruct, DataStructure}

/**
 * This now serves as a general template for
 * constituency parser
 */
trait Parser {

  /**
   * all the method edits scheduler directly (past records are kept)
   * however they do return a string indicating the changed/added column's name
   *
   * @param newColumn Enter the name that you want the newly generated column to be
   *
   * @return the generated column name (columnName if it's specified)
   */
  def parse(newColumn: Option[String] = None, struct: DataStruct): DataContainer with Parser

}