package files

import files.filetypes.output.CSVOutput
import files.structure.DataStructure
import processing.buffers.file.{FileOutputFormat, FileBuffer}
import processing.{Sequential, Scheduler}
import processing.buffers.BufferConfig
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
 * @param ignoreFileName it determines whether file name information if kept (put in a column) or not,
 *                       could be used in conjunction with fileNameColumn
 * @param fileNameColumn this column is only useful if ignoreFileName = true
 * @param core this indicates how many parallel cores you want Tinker to run on, in this version
 *             we don't support different threading numbers for different process (but it's down the road)
 *             By inputing a core number, you automatically opt for parallelism
 */
abstract class DataContainer(val f: Option[String] = None,
                              val header: Boolean = true,
                              val fuzzyMatch: Option[Int] = None,
                              val rTaskSize: Option[Int] = None,
                              val ignoreFileName: Boolean = false,
                              val fileNameColumn: Option[String] = None,
                              val core: Option[Int] = None) {

  import RowTypes._

  /* constructor */

  /* scheduler is inside every dataContainer, the FileOps on dataContainer is the FileOps on scheduler */
  var scheduler = if (core.isEmpty) defaultSchedulerConstructor(1)
                  else parallelSchedulerConstructor(core.get)

  if (!ignoreFileName) {
    scheduler.opSequence.push(unify(fileNameColumn))
  } else {
    scheduler.opSequence.push(strip)
  }

  lazy val data = scheduler.opSequence.top

  //@deprecated will be removed for the next release
  lazy val strippedData = if (scheduler.opSequence.nonEmpty) scheduler.opSequence.pop() else strip

  //only used when timer is activated
  def taskSize: Option[Int] = if (rTaskSize.nonEmpty) rTaskSize
  else Some(strip.length)

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

  /* Native Operation Methods */
  /* Those are methods that very essential to DataContainer that can't be put in FileOp*/

  def toCSV(filePath: Option[String] = None, fileAppend: Option[Boolean] = Some(true)): DataContainer = {

    val nScheduler = if (core.isEmpty) defaultSchedulerConstructor(1)
                        else parallelSchedulerConstructor(core.get)

    nScheduler.config = scheduler.config

    if (filePath.nonEmpty)
      nScheduler.config.filePath = filePath

    if (fileAppend.nonEmpty)
      nScheduler.config.fileAppend = fileAppend.get

    nScheduler.graphFlows = scheduler.graphFlows
    nScheduler.opSequence = scheduler.opSequence

    scheduler = nScheduler

    this
  }

  def toTab(filePath: Option[String] = None, fileAppend: Option[Boolean] = Some(true)): DataContainer = {
    val nScheduler = if (core.isEmpty) tabSeqSchedulerConstructor(1)
                          else tabParallelSchedulerConstructor(core.get)

    nScheduler.config = scheduler.config

    if (filePath.nonEmpty)
      nScheduler.config.filePath = filePath

    if (fileAppend.nonEmpty)
      nScheduler.config.fileAppend = fileAppend.get

    nScheduler.graphFlows = scheduler.graphFlows
    nScheduler.opSequence = scheduler.opSequence

    scheduler = nScheduler

    this
  }

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