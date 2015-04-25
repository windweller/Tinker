package nlp.ngram

import newFiles.filetypes.FileImplicits

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Aimingnie on 4/23/15
 */
trait Unigram extends Ngram {

  //current implementation requires header
  def getTokenNumber: mutable.HashMap[String, Int] = {
    val it = data.iterators.head
    val result = mutable.HashMap.empty[String, Int]

    it.foreach { group =>
      var unigramCount = 0
      val itr = group._2
      while (itr.hasNext) {
        val row = itr.next()
        if (row.nonEmpty) {
          val nonemptyRowText = row.head //this is the not flexible part, you keep getting the first because
          //tweets you are handlign has that!
          unigramCount += nonemptyRowText._2.split(" ").length
        }
      }
      result += (group._1 -> unigramCount)
    }

    result
  }

}
