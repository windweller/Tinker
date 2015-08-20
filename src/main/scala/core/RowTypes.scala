package core

import scala.collection.mutable

/**
 * Created by anie on 8/17/2015.
 */
object RowTypes {
  type NormalRow = mutable.HashMap[String, String] //enforced HashMap for it's eC performance
  type RowIterator = Iterator[NormalRow]
}
