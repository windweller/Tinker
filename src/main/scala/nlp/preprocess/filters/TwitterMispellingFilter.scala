package nlp.preprocess.filters

import com.github.tototoshi.csv.CSVWriter

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 6/13/2015.
 */
trait TwitterMispellingFilter extends Filter {

    var mispellingdict = mutable.HashMap.empty[String, String]

    val doc = scala.io.Source.fromFile("E:\\Allen\\R\\emnlp2015\\mispellingDic.txt").getLines()

    doc.foreach { line =>
      val parts = line.split("->")
      val value = parts(0).trim
      val keys = parts(1).split("\\|")
      keys.foreach(k => mispellingdict.put(k.trim, value))
    }

  def preprocess(saveLoc: String): Unit = {
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)
    val it = data.strippedData

    val result = ArrayBuffer.empty[Seq[String]]
    var counts = 0

    it.foreach { row =>
      if (row.get(struct.target.get).nonEmpty) {
         val sen = row(struct.target.get).split(" ").map { word =>
           val replaceWord = mispellingdict.get(word)
           if (replaceWord.nonEmpty) counts += 1
           replaceWord.getOrElse(word)
         }.mkString(" ")
        result += Seq(struct.getIdValue(row).getOrElse("id"), sen) ++ struct.getKeepColumnsValue(row).get
      }
    }

    println(counts)

    output.writeAll(result.toSeq)
  }
}
