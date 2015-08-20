package nlp.preprocess.tokenization

import core.DataContainer
import core.structure.{DataStruct, DataStructure}
import processing.Scheduler
import utils.Global.Implicits._

/**
 * Tokenizer breaks down text into individual sentences
 *
 */
abstract class Tokenizer(val data: DataContainer,
                         val struct: DataStructure)(implicit val pscheduler: Option[Scheduler] = None) {

  val scheduler = pscheduler.getOrElse(defaultSchedulerConstructor(4, new DataStruct()))

  def exec(): Unit = scheduler.exec()
  def save(): Unit = exec()

  /* shortcut for file save */
  def exec(filePath: Option[String], fileAppend: Boolean = true): Unit = {
    scheduler.config.filePath = filePath
    scheduler.config.fileAppend = fileAppend
    exec()
  }
  def save(filePath: Option[String], fileAppend: Boolean = true) = exec(filePath, fileAppend)

  //could cause problem
  def tokenize(): Tokenizer

}
