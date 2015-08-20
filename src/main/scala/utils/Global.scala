package utils

import files.filetypes.output.{TabOutput, CSVOutput}
import core.structure.DataStruct
import processing.buffers.BufferConfig
import processing.buffers.file.{FileOutputFormat, FileBuffer}
import processing.{Parallel, Operation, Scheduler, Sequential}
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