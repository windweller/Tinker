package files

import files.structure.DataStructure
import processing.Scheduler
import utils.Global.Implicits._
import scala.annotation.tailrec
import scala.collection.{AbstractIterator, mutable}
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
 * @param rTaskSize Right task size (how many rows), for timer
 *
 */
abstract class DataContainer(val f: Option[String] = None,
                              val header: Boolean = true,
                              val fuzzyMatch: Option[Int] = None,
                              val rTaskSize: Option[Int] = None) {

  import RowTypes._

  /* constructor */
  val scheduler = defaultSchedulerConstructor()

  lazy val data = if (scheduler.opSequence.nonEmpty) scheduler.opSequence.pop() else unify()
  lazy val strippedData = if (scheduler.opSequence.nonEmpty) scheduler.opSequence.pop() else strip

  def taskSize: Option[Int] = if (rTaskSize.nonEmpty) rTaskSize
                                   else Some(strip.length)

  //only used when timer is activated
  val taskSizeActions: ArrayBuffer[(Int) => Int] = ArrayBuffer.empty[(Int) => Int]


  /* normal method (BufferConfig alreayd passed in from Scheduler) */
  def exec(struct: Option[DataStructure]): Unit = {
    if (scheduler.opSequence.isEmpty) scheduler.opSequence.push(strippedData)
    scheduler.exec(struct)
  }
  def save(struct: Option[DataStructure]): Unit = exec(struct)


  /* shortcut for file save */
  def exec(filePath: Option[String], fileAppend: Boolean = true, struct: Option[DataStructure] = None): Unit = {
    scheduler.config.filePath = filePath
    scheduler.config.fileAppend = fileAppend
    if (scheduler.opSequence.isEmpty) scheduler.opSequence.push(strippedData)
    exec(struct)
  }
  def save(filePath: Option[String], fileAppend: Boolean = true, struct: Option[DataStructure] = None) = exec(filePath, fileAppend, struct)

  /* Core methods */

  //leave implementation details to Doc or other services
  def iteratorMap: mutable.HashMap[String, RowIterator] = mutable.HashMap.empty[String, RowIterator]

  /**
   * incorporate the file info into a column
   * @param fileColName default value is "fileName"
   * @return
   */
  def unify(fileColName: Option[String] = None): Iterator[NormalRow] = new AbstractIterator[NormalRow] {

    val itm = iteratorMap.iterator
    var cit = itm.next()

    override def hasNext: Boolean = checkEmpty(itm.hasNext || cit._2.hasNext)

    override def next(): NormalRow = {
      cit._2.next() += (fileColName.getOrElse("file_name") -> cit._1)
    }

    @tailrec
    private[this] def checkEmpty(status: Boolean): Boolean = {
      if (!status)
        false
      else {
        if (cit._2.hasNext)
          true
        else if (itm.hasNext) {
          cit = itm.next()
          if (cit._2.hasNext) true
          else checkEmpty(itm.hasNext || cit._2.hasNext)
        }
        else false
      }
    }
  }

  //discard the file information
  def strip: Iterator[NormalRow] = new AbstractIterator[NormalRow] {

    var it = iteratorMap.toIterator
    var currentIt = it.next()._2

    override def hasNext: Boolean = checkEmpty(it.hasNext || currentIt.hasNext)

    override def next(): NormalRow = {
      currentIt.next()
    }

    @tailrec
    private[this] def checkEmpty(status: Boolean): Boolean = {
      if (!status)
        false
      else {
        if (currentIt.hasNext)
          true
        else if (it.hasNext) {
          currentIt = it.next()._2
          if (currentIt.hasNext) true
          else checkEmpty(it.hasNext)
        }
        else false
      }
    }
  }

}

object RowTypes {
  type NormalRow = mutable.HashMap[String, String] //enforced HashMap for it's eC performance
  type RowIterator = Iterator[NormalRow]
}