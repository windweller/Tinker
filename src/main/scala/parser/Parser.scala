package parser


import files.DataContainer
import files.structure.DataStructure

/**
 * All operation based class takes in implicit parameter scheduler
 * Parser contains any operation that either matches a sentence
 * or parses a sentence
 *
 * It can both add to scheduler or provide a function
 * so user can call and combine/use by themselves
 *
 * any action-based class uses scheduler internally provided by DataContainer
 * every data container creates its own scheduler and is independent from other
 */
abstract class Parser(val data: DataContainer, val struct: DataStructure) {

  /**
   * all the method edits scheduler directly (past records are kept)
   * however they do return a string indicating the changed/added column's name
   *
   * @param columnName Enter the name that you want the newly generated column to be
   *
   * @return the generated column name (columnName if it's specified)
   */
  def parse(columnName: Option[String] = None): String

}