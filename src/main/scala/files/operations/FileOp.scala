package files.operations

import files.DataContainer

/**
 * Specifies file operations including
 * search(return specific line - even file name)
 * split: split the file based on certain criteria
 * compress: compress columns together
 */
trait FileOp extends DataContainer {

  //not important
  def search(piece: String):Unit = {}

//  def split()


}
