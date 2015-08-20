package nlp.preprocess.tokenization.impl

import java.io.StringReader
import core.RowTypes
import edu.stanford.nlp.ling.HasWord
import edu.stanford.nlp.process.DocumentPreprocessor
import RowTypes._
import nlp.preprocess.tokenization.Tokenizer
import scala.collection.{mutable, AbstractIterator}
import utils.ParameterCallToOption.Implicits._
/**
 * Created by anie on 5/22/2015
 *
 * Annotation: parallelizable
 */
trait Stanford extends Tokenizer {


  /**
   * tokenize() will disregard all information of the previous
   * table except group information (that came with the filename)
   * and Keep columns in data structure
   * @return
   */
  override def tokenize(): Tokenizer with Stanford = {
    val it = splitSentences()
    scheduler.opSequence.push(it)
    new Tokenizer(data, struct)(scheduler) with Stanford
  }

  def splitSentences() = new AbstractIterator[NormalRow] {

    val it = data.data //picks up the right data

    val tempRows = mutable.Queue.empty[NormalRow]

    override def hasNext: Boolean = it.hasNext || tempRows.nonEmpty


    override def next(): NormalRow = {
      if (tempRows.isEmpty) {
        val row = it.next()

        val processorIt = new DocumentPreprocessor(new StringReader(row(struct.target.get)))
        val pit = processorIt.iterator()

        //put all sentences in
        while (pit.hasNext) {
          val sentence = formSentence(pit.next())
          val result = mutable.HashMap[String, String]("sentence" -> sentence)
          if (struct.getKeepColumnsKeyValuePairs(row).nonEmpty)
            result ++= struct.getKeepColumnsKeyValuePairs(row).get
          tempRows.enqueue(result)
        }
        next()
      }
      else tempRows.dequeue()
    }
  }

  private[this] def formSentence(list: java.util.List[HasWord]): String = {
    var sentence = ""
    val it = list.iterator()
    while (it.hasNext)
      sentence += it.next().word() + " "
    sentence
  }

}
