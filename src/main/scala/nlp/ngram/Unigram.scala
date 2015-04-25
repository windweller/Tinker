package nlp.ngram

import newFiles.filetypes.FileImplicits

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Aimingnie on 4/23/15
 */
trait Unigram extends Ngram with FileImplicits{

  //current implementation requires header
  def getTokenNumber: mutable.HashMap[String, Int] = {
    val it = data.iterators.head
    val result = mutable.HashMap.empty[String, Int]
    var currentFileName = ""

    while(it.hasNext) {
      val row = it.next() //get text
      if (row.nonEmpty) {
        val nonemptyRowText = row.head
        if (currentFileName == "") currentFileName =
        result += (currentFileName -> (result.getOrElse(currentFileName, 0) + nonemptyRowText._2.split(" ").length))
      }
    }
    result
  }

}
