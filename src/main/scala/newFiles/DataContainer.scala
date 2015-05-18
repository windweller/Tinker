package newFiles

import newProcessing.Scheduler

import scala.annotation.tailrec
import scala.collection.AbstractIterator
import scala.collection.mutable
import utils.Global.Implicits._

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
 * @param pscheduler If you import Global.Implicits._, you will share the states
 *                   of all computations (which could be dangerous). Otherwise,
 *                   every DataContainer will have its own scheduler
 *
 */
abstract class DataContainer(val f: Option[String] = None,
                              val header: Boolean = true,
                              val fuzzyMatch: Option[Int] = None,
                              val rTaskSize: Option[Int] = None)(implicit val pscheduler: Option[Scheduler] = None) {

  import RowTypes._


  /* constructor */
  val scheduler = pscheduler.getOrElse(defaultSchedulerConstructor())
  lazy val data = if (scheduler.opSequence.nonEmpty) scheduler.opSequence.pop() else flatten()
  lazy val strippedData = if (scheduler.opSequence.nonEmpty) scheduler.opSequence.pop() else unify

  def taskSize: Option[Int] = if (rTaskSize.nonEmpty) rTaskSize
                                   else Some(flatten().length)

  //only used when timer is activated
  val taskSizeActions: ArrayBuffer[(Int) => Int] = ArrayBuffer.empty[(Int) => Int]


  /* normal method (BufferConfig alreayd passed in from Scheduler) */
  def exec(): Unit = scheduler.exec()
  def save(): Unit = exec()


  /* shortcut for file save */
  def exec(filePath: Option[String], fileAppend: Boolean = true): Unit = {
    scheduler.config.filePath = filePath
    scheduler.config.fileAppend = fileAppend
    exec()
  }
  def save(filePath: Option[String], fileAppend: Boolean = true) = exec(filePath, fileAppend)


  /* Core methods */

  //leave implementation details to Doc or other services
  def iteratorMap: mutable.HashMap[String, RowIterator] = mutable.HashMap.empty[String, RowIterator]

  /**
   * incorporate the file info into a column
   * @param fileColName default value is "fileName"
   * @return
   */
  def flatten(fileColName: Option[String] = None): Iterator[NormalRow] = new AbstractIterator[NormalRow] {

    val itm = iteratorMap.iterator
    var cit = itm.next()

    override def hasNext: Boolean = itm.hasNext || cit._2.hasNext

    @tailrec
    override def next(): NormalRow = {
      if (!cit._2.hasNext && itm.hasNext) {
        cit = itm.next()
        next()
      }
      else cit._2.next() += (fileColName.getOrElse("fileName") -> cit._1)
    }
  }

  //discard the file information
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
  type NormalRow = mutable.HashMap[String, String] //enforced HashMap for it's eC performance
  type RowIterator = Iterator[NormalRow]
}