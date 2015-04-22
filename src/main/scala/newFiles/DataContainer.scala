package newFiles

import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.immutable.ParVector

/**
 * Redesign of old DataContainer
 * emphasize on iterator operation
 *
 * The saving functionality is integrated with
 * Operation's exec() method. We do not offer
 * real save() method, but maybe an alias that maps
 * to exec()
 */
abstract class DataContainer(val f: Option[String] = None, val header: Option[Boolean] = Some(true)) {

  import rowTypes._

  val iterators: ArrayBuffer[RowIterator] = ArrayBuffer.empty[RowIterator]

}

object rowTypes {
  type Key = Option[String]
  type Value = String
  type NormalRow = ParVector[(Key, Value)] //key, value
  type RowIterator = Iterator[NormalRow]
}