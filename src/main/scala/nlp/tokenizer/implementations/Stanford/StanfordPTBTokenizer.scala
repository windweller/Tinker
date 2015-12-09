package nlp.tokenizer.implementations.Stanford

import java.io.StringReader

import core.structure.{Schema, DataSelect}
import core.{DataContainer, TypedRow}
import edu.stanford.nlp.ling.HasWord
import edu.stanford.nlp.process.DocumentPreprocessor
import nlp.tokenizer.Tokenizer
import scala.collection.{AbstractIterator, mutable}

/**
 * Created by anie on 7/28/2015
 */
trait StanfordPTBTokenizer extends Tokenizer {

  this: DataContainer =>

  /**
   * tokenize() will disregard the original sentence's column
   * and replace it with new column named "sentence"
   *
   * @return
   */
  override def tokenize(newColumn: Option[String], select: DataSelect): DataContainer with Tokenizer = {
    val it = splitSentences(newColumn, select)
    scheduler.opSequence.push(it)
    this
  }

  private[this] def splitSentences(newColumn: Option[String], select: DataSelect) = new AbstractIterator[TypedRow] {

    implicit val schema = Schema()

    val it = data

    val tempRows = mutable.Queue.empty[TypedRow]

    override def hasNext: Boolean = it.hasNext || tempRows.nonEmpty

    override def next(): TypedRow = {

      if (tempRows.isEmpty) {
        val row = it.next()

        val sentences = row.remove(select.target.get)  //remove from original

        val processorIt = new DocumentPreprocessor(new StringReader(sentences.get))
        val pit = processorIt.iterator()

        //put all sentences in
        while (pit.hasNext) {
          val sentence = formSentence(pit.next())
          //duplicate existing row, add the pair to duplicated row
          val nrow = row.duplicate addValuePair (newColumn.getOrElse("sentence") -> sentence, 'String)
          tempRows.enqueue(nrow)
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
