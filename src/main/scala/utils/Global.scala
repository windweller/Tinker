package utils

import core.structure.{DataStruct, Schema}
import files.filetypes.output.{CSVOutput, TabOutput}
import processing.buffers.BufferConfig
import processing.buffers.file.FileBuffer
import processing.{Parallel, Scheduler, Sequential}
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

    //provide a global implicit schema that's easy to import
    //so users don't have to manually create if chosen not to
    //this should be able to reduce crashes or bugs
    implicit lazy val schema: Schema = Schema()

    //every time a DataContainer is constructed, this method is called
    def defaultSchedulerConstructor(core: Int, ds: DataStruct): Scheduler = {
      val s = new Scheduler(core)(BufferConfig()) with Sequential with FileBuffer with CSVOutput
      s.dataStructure = ds
      s
    }

    def parallelSchedulerConstructor(core: Int, ds: DataStruct): Scheduler = {
      val s = new Scheduler(core)(BufferConfig()) with Parallel with FileBuffer with CSVOutput
      s.dataStructure = ds
      s
    }

    def tabSeqSchedulerConstructor(core: Int, ds: DataStruct): Scheduler = {
      val s = new Scheduler(core)(BufferConfig()) with Sequential with FileBuffer with TabOutput
      s.dataStructure = ds
      s
    }

    def tabParallelSchedulerConstructor(core: Int, ds: DataStruct): Scheduler = {
      val s =  new Scheduler(core)(BufferConfig()) with Parallel with FileBuffer with TabOutput
      s.dataStructure = ds
      s
    }

  }

}