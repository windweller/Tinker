package files.operations

import files.{Doc, DataContainer}
import files.DataContainerTypes._

/**
 * Specifies file operations including
 * search(return specific line - even file name)
 * split: split the file based on certain criteria
 * compress: compress columns together
 *
 * Apply to Iterator level, so no need for parallelism
 */
trait FileOp extends DataContainer {

  //not important
  def search(piece: String):Unit = {}

  //combine two dataContainer
  //if no common column is found, we just combine them
  def combine(dataB: DataContainer, joinColNum: Option[Int] = None,
              joinColStr: Option[String] = None): Unit = {

  }



  //split the dataset into several files
  //based on a criterion
  def split(): Unit = {

  }


}
