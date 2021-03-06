package nlp.preprocess.filters

import application.Application
import com.github.tototoshi.csv.CSVWriter
import utils.Timer

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 6/13/2015.
 */
trait TwitterMispellingFilter extends Filter {

    val doc = scala.io.Source.fromURL(Application.file("mispellingDic.txt"))
      .getLines()
    var mispellingdict = mutable.HashMap.empty[String, String]

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
        val keep = struct.getKeepColumnsValue(row)
        val seqnormal = Seq(struct.getIdValue(row).getOrElse("id"), sen)
        if(keep.isEmpty)
          result += seqnormal
        else
          result += seqnormal ++ keep.get
        if(Application.verbose) Timer.completeOne
      }
    }

    println(counts)

    output.writeAll(result.toSeq)
  }
}
