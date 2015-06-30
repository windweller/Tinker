package utils

import files.filetypes.output.CSVOutput
import processing.buffers.BufferConfig
import processing.buffers.file.FileBuffer
import processing.{Scheduler, Sequential}
import utils.ParameterCallToOption.Implicits._

import scala.collection.mutable

/**
 * Created by anie on 4/22/2015
 *
 * This is a special Global object
 * that contains information (like generated
 * temporary file's name)
 */
object Global {

  //tempFiles implemented as a List, last in (append) first out
  val tempFiles: mutable.Stack[String] = mutable.Stack()


  object Implicits {

    //this might create a shared state mess
    implicit val scheduler = defaultSchedulerConstructor()

    //every time a DataContainer is constructed, this method is called
    def defaultSchedulerConstructor(): Scheduler =
          new Scheduler(4)(BufferConfig()) with Sequential with FileBuffer with CSVOutput

  }

}