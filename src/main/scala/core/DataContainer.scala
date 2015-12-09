package core

import core.structure.{DataSelect, DataStruct, Schema}
import processing.Scheduler
import utils.FailureHandle
import utils.Global.Implicits._

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.collection.{AbstractIterator, mutable}

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
 * @param ignoreFileName it determines whether file name information if kept (put in a column) or not,
 *                       could be used in conjunction with fileNameColumn
 * @param fileNameColumn this column is only useful if ignoreFileName = true
 * @param core this indicates how many parallel cores you want Tinker to run on, in this version
 *             we don't support different threading numbers for different process (but it's down the road)
 *             By inputing a core number, you automatically opt for parallelism
 * @param schema pass in a schema that specifies how columns should be parsed (as string or double). No need
 *               to specify every single one, just ones that need to be taken care of, such as ID column.
 */
abstract class DataContainer(val f: Option[String] = None,
                              val header: Boolean = true,
                              val fuzzyMatch: Option[Int] = None,
                              val taskSize: Option[Int] = None,
                              val ignoreFileName: Boolean = false,
                              val fileNameColumn: Option[String] = None,
                              val core: Option[Int] = None,
                              val schema: Schema = Schema())(implicit val pscheduler: Option[Scheduler] = None) extends FailureHandle {

  /* constructor */

  //dataStruct of the data, should be bundled to change each time
  //will be implicitly passed into TypedRow when change happens
  implicit val ds: DataStruct

  /* scheduler is inside every dataContainer, the FileOps on dataContainer is the FileOps on scheduler */
  var scheduler = if (pscheduler.nonEmpty) {
    pscheduler.get.dataStructure = ds; pscheduler.get
  }
  else if (core.isEmpty) defaultSchedulerConstructor(1, ds)
  else parallelSchedulerConstructor(core.get, ds)

  if (!ignoreFileName) {
    scheduler.opSequence.push(unify(fileNameColumn))
  } else {
    scheduler.opSequence.push(strip)
  }

  lazy val data = scheduler.opSequence.top

  //@depcrecated: the fucntionality is taken care of for the "!ignoreFileName" command
  lazy val strippedData = if (scheduler.opSequence.nonEmpty) scheduler.opSequence.pop() else strip

  //only used when timer is activated
  def rTaskSize: Option[Int] = if (taskSize.nonEmpty) taskSize
  else Some(strip.length)

  val taskSizeActions: ArrayBuffer[(Int) => Int] = ArrayBuffer.empty[(Int) => Int]

  /* save method (BufferConfig alreayd passed in from Scheduler) */

  def exec(select: Option[DataSelect], ignore: Option[DataSelect]): Unit = {

    if (select.nonEmpty && ignore.nonEmpty) fatal("You can't choose both select and ignore")

    if (scheduler.opSequence.isEmpty) scheduler.opSequence.push(strippedData)
    scheduler.exec(select, ignore)
  }

  def save(select: Option[DataSelect], ignore: Option[DataSelect]): Unit = exec(select, ignore)

  /* shortcut for file save */
  def exec(filePath: Option[String], fileAppend: Boolean = true, select: Option[DataSelect] = None,
           ignore: Option[DataSelect] = None): Unit = {
    scheduler.config.filePath = filePath
    scheduler.config.fileAppend = fileAppend
    if (scheduler.opSequence.isEmpty) scheduler.opSequence.push(strippedData)
    exec(select, ignore)
  }

  def save(filePath: Option[String], fileAppend: Boolean = true,
           select: Option[DataSelect] = None, ignore: Option[DataSelect] = None) = exec(filePath, fileAppend, select, ignore)

  /**
   * This will sequentially execute everything and return
   * table value (take everything into memory)
   * @return
   */
//  def collect: Table = {
//
//  }


  /* Shortcut Methods */

  def toCSV: DataContainer = {

    val nScheduler = if (core.isEmpty) defaultSchedulerConstructor(1, ds)
    else parallelSchedulerConstructor(core.get, ds)
    exchangeScheduler(nScheduler)
    this
  }

  def toTab: DataContainer = {
    val nScheduler = if (core.isEmpty) tabSeqSchedulerConstructor(1, ds)
    else tabParallelSchedulerConstructor(core.get, ds)
    exchangeScheduler(nScheduler)
    this
  }

  //this private function affects outside parameter
  private[this] def exchangeScheduler(nScheduler: Scheduler): Unit = {
    nScheduler.config = scheduler.config
    nScheduler.graphFlows = scheduler.graphFlows
    nScheduler.opSequence = scheduler.opSequence

    scheduler = nScheduler
  }

  /* Core methods */

  //leave implementation details to Doc or other services
  def iteratorMap: mutable.HashMap[String, Iterator[TypedRow]] = mutable.HashMap.empty[String, Iterator[TypedRow]]

  /**
   * incorporate the file info into a column
   * @param fileColName default value is "fileName"
   * @return
   */
  def unify(fileColName: Option[String] = None): Iterator[TypedRow] = new AbstractIterator[TypedRow] {

    //provide implicit sm for +=
    implicit val sm: Schema = schema

    val itm = iteratorMap.iterator
    var cit = itm.next()

    override def hasNext: Boolean = checkEmpty(itm.hasNext || cit._2.hasNext)

    override def next(): TypedRow = {
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
  def strip: Iterator[TypedRow] = new AbstractIterator[TypedRow] {

    var it = iteratorMap.toIterator
    var currentIt = it.next()._2

    override def hasNext: Boolean = checkEmpty(it.hasNext || currentIt.hasNext)

    override def next(): TypedRow = {
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
