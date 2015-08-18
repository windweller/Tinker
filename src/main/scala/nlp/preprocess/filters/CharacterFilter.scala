package nlp.preprocess.filters

import com.github.tototoshi.csv.CSVWriter
import utils.TabPrinter

import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 4/27/2015
 */
trait CharacterFilter extends Filter{

  var character_limit = 4

  override def preprocess(saveLoc: String): Unit = {
    val printer = TabPrinter(saveLoc)
    val it = data.iteratorMap
    val result = ArrayBuffer.empty[Seq[String]]

    var z = 0

    it.foreach { group =>
      val itr = group._2
      while (itr.hasNext) {
        val row = itr.next()
        if (row.nonEmpty) {
          if (row.get(struct.target.get).nonEmpty) {
            val tweet = row(struct.target.get).replaceAll("\\W \\S", "") //replace non-English characters
            if (tweet.split(" ").length >= character_limit) {
              printer.print(Seq(struct.getIdValue(row).getOrElse(group._1), tweet))
            }
          }
        }
      }
    }
    printer.close()
  }

}
