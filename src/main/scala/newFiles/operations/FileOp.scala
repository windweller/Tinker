package newFiles.operations

import com.github.tototoshi.csv.CSVWriter
import newFiles.DataContainer
import newFiles.structure.DataStructure
import newProcessing.Operation

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import utils.collections.ArrayUtil._


/**
 * Created by anie on 4/18/2015.
 */
trait FileOp extends DataContainer {   // with Operation

  def combine(data2: DataContainer): DataContainer with FileOp = {
    this
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

//        dataPoints += 1
//        if (id.isEmpty)
//          id = struct.getIdValue(row)
//        else if (id.get != struct.getIdValue(row).get) {
//          output.writeRow(Seq(id.get) ++ sumForGroup.values.toSeq.map(v => v/dataPoints))
//          dataPoints = 0
//          id = struct.getIdValue(row)
//          sumForGroup.clear() //clean hashmap
//        }
      }

      //when over, do it again
//      output.writeAll(sumForGroup.values.toSeq.map(v => v/dataPoints))
    }
    println(sumForGroup.size)
  }


}