package utils

import newProcessing.buffers.FileBuffer
import newProcessing.{Parallel, Scheduler}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 4/22/2015
 *
 * This is a special Global object
 * that contains information (like generated
 * temporary file's name)
 */
object Global {

  //tempFiles implemented as a queue, first in first out
  object Implicits {
    implicit val scheduler = new Scheduler with Parallel with FileBuffer
  }

}
