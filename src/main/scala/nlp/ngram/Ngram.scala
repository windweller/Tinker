package nlp.ngram

import newFiles.DataContainer
import newFiles.structure.DataStructure

import scala.collection.mutable

/**
 * Created by Aimingnie on 4/23/15
 *
 * All Ngram features/methods are implemented by traits
 *
 * This is the controller, while trait provides individual functionality
 *
 */
abstract class Ngram(val data: DataContainer, val struct: DataStructure, val ngram: Vector[Int] = Vector(3)) {

  def getUnigramTokenNumber: mutable.HashMap[String, Int]

  val hs = mutable.HashSet.empty[String]


}
