package nlp.ngram

import scala.collection.mutable

/**
 * Created by Aimingnie on 4/23/15
 */
trait Unigram extends Ngram {

  //current implementation requires header
  def getTokenNumber: mutable.HashMap[String, Int] = {
    val it = data.iterator
    val result = mutable.HashMap.empty[String, Int]

    it.foreach { group =>
      var unigramCount = 0
      val itr = group._2
      while (itr.hasNext) {
        val row = itr.next()
        if (row.nonEmpty) {
          val nonemptyRowText = row(struct.getTarget.get)
          unigramCount += nonemptyRowText.split(" ").length
        }
      }
      result += (group._1 -> unigramCount)
    }

    result
  }
}
