package nlp.basic.sentence

import java.io.StringReader

import com.github.tototoshi.csv.CSVWriter
import edu.stanford.nlp.ling.HasWord
import edu.stanford.nlp.process.DocumentPreprocessor
import nlp.basic.Sentence

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Aimingnie on 4/25/15.
 */
trait Split extends Sentence {

  def countSentences(): mutable.HashMap[String, Int] = {
    val it = data.iteratorMap
    val result = mutable.HashMap.empty[String, Int]

    it.foreach { group =>
      var sentenceCount = 0
      val itr = group._2
      while (itr.hasNext) {
        val row = itr.next()
        if (row.nonEmpty) {
          val processorIterator = new DocumentPreprocessor(new StringReader(row(struct.getTarget.get)))
          val sentences = ArrayBuffer.empty[java.util.List[HasWord]]

          val it = processorIterator.iterator()

          while (it.hasNext) {
            sentences += it.next()
          }
          sentenceCount += sentences.length
        }
      }
      result += (group._1 -> sentenceCount)
    }

    result
  }

  def splitSentences(saveLoc: String): Unit = {

    val it = data.iteratorMap
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    val result = ArrayBuffer.empty[Seq[String]]

    it.foreach { group =>
      val itr = group._2
      while (itr.hasNext) {
        val row = itr.next()
        if (row.nonEmpty) {
          val processorIterator = new DocumentPreprocessor(new StringReader(row(struct.getTarget.get)))
          val sentences = ArrayBuffer.empty[java.util.List[HasWord]]

          val it = processorIterator.iterator()

          while (it.hasNext) {
            sentences += it.next()
          }

          sentences.foreach{s =>
            result += Seq(struct.getIdValue(row).getOrElse(group._1), formSentence(s))
          }

        }
      }
    }

    output.writeAll(result)

  }

  private[this] def formSentence(list: java.util.List[HasWord]): String = {
    var sentence = ""
    val it = list.iterator()
    while (it.hasNext)
      sentence += it.next().word() + " "
    sentence
  }

}
