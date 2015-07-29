package tokenizer.implementations.Stanford

import java.io.StringReader

import edu.stanford.nlp.ling.HasWord
import edu.stanford.nlp.process.DocumentPreprocessor
import files.DataContainer
import files.RowTypes._
import files.structure.DataSelect$
import tokenizer.Tokenizer

import scala.collection.{AbstractIterator, mutable}

/**
 * Created by anie on 7/28/2015.
 */
trait StanfordPTBTokenizer extends Tokenizer {

  this: DataContainer =>

  /**
   * tokenize() will disregard all information of the previous
   * table except group information (that came with the filename)
   * and Keep columns in data structure
   * @return
   */
  override def tokenize(struct: DataSelect): DataContainer with Tokenizer = {
    val it = splitSentences(struct)
    scheduler.opSequence.push(it)
    this
  }

  def splitSentences(struct: DataSelect) = new AbstractIterator[NormalRow] {

    val it = data //picks up the right data

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
          row += ("sentence" -> sentence)
          tempRows.enqueue(row)
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
