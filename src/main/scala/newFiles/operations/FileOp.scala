package newFiles.operations

import com.github.tototoshi.csv.CSVWriter
import newFiles.DataContainer
import newFiles.RowTypes.{RowIterator, NormalRow}
import newFiles.structure.{StructureUtils, DataStructure}
import utils.FailureHandle

import scala.collection.immutable.HashMap
import scala.collection.{AbstractIterator, mutable}
import scala.collection.mutable.ArrayBuffer
import utils.collections.ArrayUtil._

/**
 * Created by anie on 4/18/2015
 *
 * Any FileOp will break the HashMap[String, RowIterator] down
 * and flatten them. The String provided by HashMap will become
 * an extra field to iterator
 *
 * Also every operation will return a new DataContainer
 *
 * FileOp does not accept DataStructure in order to improve flexibility and
 * more direct user interface (but it does take use of StructureUtils to extract)
 *
 */
trait FileOp extends DataContainer with StructureUtils with FailureHandle {

  def combine(data2: DataContainer): DataContainer with FileOp = {
    this
  }

  /**
   * This is groupBy
   */
  def compress(target: Option[Int], targetWithName: Option[String]): Unit = {
    val t = getSingleIntStringOption(target, targetWithName)
  }

  /**
   *
   * @param discardCols the columns that are "nominal" (such as ID) should be discarded and not enter into the mix
   * @param windowSize the size of the "queue". Don't use this function if you don't know what a sliding window is
   * @return
   */
  def compressBySlidingWindow(discardCols: Option[IndexedSeq[Int]] = None,
                              discardColsWithName: Option[IndexedSeq[String]] = None,
                              windowSize: Int): DataContainer with FileOp = {
    val it = compressBySlidingWindowIt(getMultipleIntStringOption(discardCols, discardColsWithName).get, windowSize)
    scheduler.opSequence += it
    new DataContainer(this.f, this.header, this.fuzzyMatch)(scheduler) with FileOp
  }

  /**
   *  This does not compress, this compress by sliding window
   *  producing sequence from (1,2,3,4,5) to (1,2,3), (2,3,4), (3,4,5)
   *  This does summation
   */
  private[this] def compressBySlidingWindowIt(discardCols: IndexedSeq[String], windowSize: Int): RowIterator = new AbstractIterator[NormalRow] {

    val it = data //this will retrieve the right data
    val window = mutable.Queue.empty[NormalRow]

    //initialize by filling up the window
    while (window.size < windowSize) {
      window.enqueue(it.next())
    }

    override def hasNext: Boolean = it.hasNext

    override def next(): NormalRow = {
      val result = sumQueue(window, discardCols)
      window.dequeue() //remove first one
      window.enqueue(it.next()) //add a new one
      result
    }
  }

  /**
   * Helper function for compressBySlidingWindowIt
   */
  private[this] def sumQueue(window: mutable.Queue[NormalRow], discardCols: IndexedSeq[String]): NormalRow = {
    val result = mutable.HashMap.empty[String, String]
    window.foreach { item =>
      item.foreach(col => {
        if (!discardCols.contains(col._1))
          result.put(col._1, (result.getOrElse(col._1, "0").toInt + col._2.toInt).toString)
        }
      )
    }
    result
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