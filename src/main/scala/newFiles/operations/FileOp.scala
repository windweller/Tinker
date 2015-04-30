package newFiles.operations

import newFiles.DataContainer
import newProcessing.Operation

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
 * Created by anie on 4/18/2015.
 */
trait FileOp extends DataContainer with Operation {

  def combine(data2: DataContainer): DataContainer with FileOp = {
    iterators ++= data2.iterators
    this
  }

  def averageByGroup(saveLoc: String): Unit = {
    val it = iterators.head
    val result = ArrayBuffer.empty[mutable.HashMap[String, Double]]

    it.foreach { group =>
      val itr = group._2
      val sumForGroup = mutable.HashMap.empty[String, Double]
      itr.foreach {row =>
        val it = row.iterator
        it.foreach(pair =>
          if (sumForGroup.get(pair._1).isEmpty)
            sumForGroup += (pair._1 -> pair._2.toDouble)
          else
            sumForGroup.update(pair._1, sumForGroup(pair._1) + pair._2.toDouble)
        )
      }

      result += sumForGroup
    }
  }


}
