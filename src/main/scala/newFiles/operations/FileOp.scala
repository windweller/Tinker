package newFiles.operations

import com.github.tototoshi.csv.CSVWriter
import newFiles.DataContainer
import newFiles.RowTypes.{NormalRow, RowIterator}
import newFiles.structure.{DataStructure, StructureUtils}
import utils.FailureHandle
import utils.collections.ArrayUtil._
import utils.ParameterCallToOption.implicits._

import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.{AbstractIterator, mutable}

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

  /* Logistic method */

  //remove previous action, can be chained
  def rewind(): DataContainer with FileOp = {
    scheduler.opSequence.pop()
    new DataContainer(this.f, this.header, this.fuzzyMatch, this.rTaskSize)(scheduler) with FileOp
  }

  /* Core methods */

  /**
   * Unfinished
   */
  def compressByAvg(groupByCol: Option[Int] = None, groupByColWithName: Option[String] = None,
                    discardCols: Option[IndexedSeq[Int]] = None,
                    discardColsWithName: Option[IndexedSeq[String]] = None): DataContainer with FileOp = {
    val it = compressByAvgIt(getSingleIntStringOption(groupByCol, groupByColWithName).get,
                      getMultipleIntStringOption(discardCols, discardColsWithName))
    scheduler.opSequence.push(it)
    new DataContainer(this.f, this.header, this.fuzzyMatch, this.rTaskSize)(scheduler) with FileOp
  }

  private[this] def compressByAvgIt(g: String,
                                    discardCols: Option[IndexedSeq[String]]): RowIterator = new AbstractIterator[NormalRow] {

    val it = strippedData
    var sizeOfGroup = 0
    var previousGroupTag = ""
    val group = ArrayBuffer.empty[NormalRow]
    var endState = false

    override def hasNext: Boolean = it.hasNext || !endState

    @tailrec
    override def next(): NormalRow = {
      if (!it.hasNext) {
        endState = true
        sumRows(group, Vector(g, previousGroupTag), discardCols, sizeOfGroup)
      }
      else {
        val row = it.next()
        //initialize
        if (previousGroupTag == "") previousGroupTag = row(g)
        //same group compress
        if (row(g) == previousGroupTag) {
          group += row
          //recursively call
          sizeOfGroup += 1
          next()
        }
        else {
          val result = sumRows(group, Vector(g, previousGroupTag), discardCols, sizeOfGroup)
          previousGroupTag = row(g)
          group.clear()
          group += row
          sizeOfGroup = 1
          result
        }
      }

    }
  }

  /**
   * @param idValue stored as form of Vector, but just two values, first is the value of column ID,
   *                second is the value of that column
   * @return
   */
  private[this] def sumRows(rows: ArrayBuffer[NormalRow], idValue: Vector[String],
                            discardCols: Option[IndexedSeq[String]], sizeOfGroup: Int): NormalRow = {
    val result = mutable.HashMap.empty[String, Int]
    rows.foreach { row =>
      row.foreach { col =>
        if (!discardCols.exists(e => e.contains(col._1)) &&
            idValue.head != col._1)
          result.put(col._1, result.getOrElse(col._1, 0) + col._2.toInt)
      }
    }
    result.map(col => col._1 -> (col._2.toDouble / sizeOfGroup).toString) += idValue.head -> idValue(1)
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
    scheduler.opSequence.push(it)
    new DataContainer(this.f, this.header, this.fuzzyMatch, this.rTaskSize)(scheduler) with FileOp
  }

  /**
   *  This does not compress, this compress by sliding window
   *  producing sequence from (1,2,3,4,5) to (1,2,3), (2,3,4), (3,4,5)
   *  This does summation
   */
  private[this] def compressBySlidingWindowIt(discardCols: IndexedSeq[String], windowSize: Int): RowIterator = new AbstractIterator[NormalRow] {

    val it = strippedData //this will retrieve the right data, stripped because compression doesn't need more info
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
    val result = mutable.HashMap.empty[String, Int]
    window.foreach { item =>
      item.foreach(col => {
        if (!discardCols.contains(col._1))
          result.put(col._1, result.getOrElse(col._1, 0) + col._2.toInt)
        }
      )
    }
    result.map(e => e._1 -> e._2.toString)
  }

  def countByGroup(groupCol: Option[Int] = None,
                   groupColWithName: Option[String] = None,
                    isFileName: Boolean = false): DataContainer with FileOp = {
    val colName: String = if (groupCol.nonEmpty || groupColWithName.nonEmpty) getSingleIntStringOption(groupCol, groupColWithName).get
                  else if (isFileName) "fileName"
                  else throw new Exception("You must specify how it's being grouped")

    val it = countByGroupIt(colName)
    scheduler.opSequence.push(it)
    new DataContainer(this.f, this.header, this.fuzzyMatch, this.rTaskSize)(scheduler) with FileOp
  }

  //TODO: not entirely working, but it's probably printing's fault
  //this algorithm works fine if you directly print here
  private[this] def countByGroupIt(coln: String): RowIterator = new AbstractIterator[NormalRow] {

    val it = unify()

    var count = 0
    var previousGroupTag = ""
    var endState = false

    override def hasNext: Boolean = it.hasNext || !endState

    override def next(): NormalRow = {
      if (!it.hasNext) {
        endState = true
        mutable.HashMap[String, String](previousGroupTag -> count.toString)
      }
      else {
        val row = it.next()
        //initialize
        if (previousGroupTag == "") previousGroupTag = row(coln)
        //same group compress
        if (row(coln) == previousGroupTag) {
          count += 1
          next()
        }
        else {
          val result = mutable.HashMap(previousGroupTag -> count.toString)
          previousGroupTag = row(coln)
          count = 1
          result
        }
      }
    }
  }

  //legacy method
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
          if (struct.ignore.get != pair._1 && struct.id.get != pair._1)
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