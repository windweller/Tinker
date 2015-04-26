package nlp.ngram

import newFiles.DataContainer
import newFiles.structure.DataStructure

import scala.collection.mutable

/**
 * Created by Aimingnie on 4/23/15
 *
 * All Ngram features/methods are implemented by subsequent
 * traits
 */
abstract class Ngram(val data: DataContainer, val struct: DataStructure) {

  def getTokenNumber: mutable.HashMap[String, Int]

}
