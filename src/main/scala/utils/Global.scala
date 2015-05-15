package utils

import newFiles.filetypes.tab.Tab
import newProcessing.buffers.FileBuffer
import newProcessing.{Parallel, Scheduler}

import scala.collection.mutable

/**
 * Created by anie on 4/22/2015
 *
 * This is a special Global object
 * that contains information (like generated
 * temporary file's name)
 */
object Global {

  //tempFiles implemented as a stack, last in first out
  val tempFiles: mutable.Stack[String] = mutable.Stack()

  object Implicits {

    //this might create a shared state mess
    implicit val scheduler = defaultSchedulerConstructor()

    def defaultSchedulerConstructor() = new Scheduler(4) with Parallel with FileBuffer with Tab
  }

}