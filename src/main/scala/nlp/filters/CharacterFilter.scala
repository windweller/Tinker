package nlp.filters

import com.github.tototoshi.csv.CSVWriter
import utils.TabPrinter

import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 4/27/2015
 */
trait CharacterFilter extends Filter{

  var character_limit = 2

  override def preprocess(saveLoc: String): Unit = {
    val printer = TabPrinter(saveLoc)
    val it = data.iteratorMap
    val result = ArrayBuffer.empty[Seq[String]]

    it.foreach { group =>
      val itr = group._2
      while (itr.hasNext) {
        val row = itr.next()
        if (row.nonEmpty) {
          val tweet = row(struct.getTarget.get)
          if (tweet.trim.nonEmpty) {
            printer.print(Seq(struct.getIdValue(row).getOrElse(group._1), tweet))
          }
        }
      }
    }
    printer.close()
  }

}
