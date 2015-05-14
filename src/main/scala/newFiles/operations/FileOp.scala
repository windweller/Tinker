package newFiles.operations

import com.github.tototoshi.csv.CSVWriter
import newFiles.DataContainer
import newFiles.RowTypes.{RowIterator, NormalRow}
import newFiles.structure.{StructureUtils, DataStructure}
import newProcessing.Operation
import utils.FailureHandle

import scala.collection.{AbstractIterator, mutable}
import scala.collection.mutable.ArrayBuffer
import utils.collections.ArrayUtil._

/**
 * Created by anie on 4/18/2015
 *
 * Any FileOp will break the HashMap[String, RowIterator] down
 * and unify them. The String provided by HashMap will become
 * an extra field to iterator
 *
 */
trait FileOp extends DataContainer with StructureUtils with FailureHandle {

  checkSchedulerStatus()

  def checkSchedulerStatus(): Unit =
    if (scheduler.isEmpty) fatal("Any FileOp activities must have scheduler")

  def combine(data2: DataContainer): DataContainer with FileOp = {
    this
  }

  /**
   * This is groupBy
   */
  def compress(target: Option[Int], targetWithName: Option[String]): Unit = {
    val t = getSingleIntStringOption(target, targetWithName)

  }

  def compressBySlidingWindow(target: Option[Int], targetWithName: Option[String], windowSize: Int): DataContainer with FileOp = {
    this
  }

  /**
   *  This does not compress, this compress by sliding window
   *  producing sequence from (1,2,3,4,5) to (1,2,3), (2,3,4), (3,4,5)
   *
   *  This does summation
   */
  private[this] def compressBySlidingWindowIt(target: Option[Int], targetWithName: Option[String], windowSize: Int): RowIterator = new AbstractIterator[NormalRow] {
    val t = getSingleIntStringOption(target, targetWithName)
    val it = flatten()
    val window = mutable.Queue.empty[NormalRow]

    //initialize by filling up the window
    while (window.size < windowSize) {

    }

    override def hasNext: Boolean = it.hasNext

    override def next(): NormalRow = {

    }
  }

  def averageByGroup(saveLoc: String, struct: DataStructure): Unit = {

    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)
    val it = iteratorMap

    val sumForGroup = mutable.HashMap.empty[String, ArrayBuffer[Double]]

    it.foreach { group =>
      val itr = group._2
//      var dataPoints = 0 //starting at 0 for every group
      var id: Option[String] = None
      itr.foreach {row =>
        val columnIt = row.iterator
        val columns = ArrayBuffer.empty[Double]
        val pairId: Option[String] = struct.getIdValue(row)
        columnIt.foreach { pair =>
          if (struct.getIgnore.get != pair._1 && struct.getId.get != pair._1)
            columns += pair._2.toDouble
        }

        if (sumForGroup.get(pairId.get).isEmpty)
          sumForGroup += (pairId.get -> columns)
        else
          sumForGroup.update(pairId.get, arrayPlusArray(sumForGroup(pairId.get), columns))
      }
    }
    println(sumForGroup.size)
  }

  /**
   * FileOp Utils
   */

}