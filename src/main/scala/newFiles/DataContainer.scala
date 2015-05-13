package newFiles

import newFiles.filetypes.FileIterator

import scala.annotation.tailrec
import scala.collection.AbstractIterator
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

  import RowTypes._

  //leave implementation details to Doc or other services
  def iteratorMap: Map[String, RowIterator]

  //erases the signature of Map, and return a unified iterator
  def unify: Iterator[NormalRow] = new AbstractIterator[NormalRow] {

    var it = iteratorMap.toIterator
    var currentIt = it.next()._2

    override def hasNext: Boolean = it.hasNext || currentIt.hasNext

    @tailrec
    override def next(): NormalRow = {
      if (!currentIt.hasNext && it.hasNext) {
        currentIt = it.next()._2
        next()
      }
      else currentIt.next()
    }
  }

}

object RowTypes {
  type NormalRow = HashMap[String, String] //enforced HashMap for it's eC performance
  type RowIterator = Iterator[NormalRow]
}