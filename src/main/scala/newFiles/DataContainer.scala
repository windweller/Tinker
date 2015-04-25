package newFiles

import newFiles.filetypes.FileIterator

import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

/**
 * Redesign of old DataContainer
 * emphasize on iterator operation
 *
 * The saving functionality is integrated with
 * Operation's exec() method. We do not offer
 * real save() method, but maybe an alias that maps
 * to exec()
 *
 * @param fuzzyMatch this gets passed to FileMapIterator, exclusive end
 */
abstract class DataContainer(val f: Option[String] = None,
                              val header: Option[Boolean] = Some(true),
                              val fuzzyMatch: Option[Int] = None) {

  import rowTypes._

  val iterators: ArrayBuffer[Map[String, RowIterator]] = ArrayBuffer.empty[Map[String, RowIterator]]

}

object rowTypes {
  type NormalRow = HashMap[String, String] //enforced HashMap for it's eC performance
  type RowIterator = Iterator[NormalRow]
}