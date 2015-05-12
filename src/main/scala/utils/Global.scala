package utils

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

  //tempFiles implemented as a queue, first in first out
  val tempFiles = mutable.Queue.empty[String]

  object Implicits {
    implicit val scheduler = new Scheduler(4) with Parallel with FileBuffer
  }

}
