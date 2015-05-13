package newFiles.operations

import com.github.tototoshi.csv.CSVWriter
import newFiles.DataContainer
import newFiles.structure.{StructureUtils, DataStructure}
import newProcessing.Operation

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import utils.collections.ArrayUtil._

/**
 * Created by anie on 4/18/2015
 *
 * Ask to pass in dataStructure
 */
trait FileOp extends DataContainer with StructureUtils {   // with Operation

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
   *  This does not compress, this compress by sliding window
   *  producing sequence from (1,2,3,4,5) to (1,2,3), (2,3,4), (3,4,5)
   */
  def compressBySlidingWindow(target: Option[Int], targetWithName: Option[String]): Unit = {
    val t = getSingleIntStringOption(target, targetWithName)

  }

  def averageByGroup(saveLoc: String, struct: DataStructure): Unit = {

    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)
    val it = iterator

    val sumForGroup = mutable.HashMap.empty[String, ArrayBuffer[Double]]

    it.foreach { group =>
      val itr = group._2
//      var dataPoints = 0 //starting at 0 for every group
      var id: Option[String] = None
      itr.foreach {row =>
        val columnIt = row.iterator
        val columns = ArrayBuffer.empty[Double]
        var pairId: Option[String] = struct.getIdValue(row)
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


}