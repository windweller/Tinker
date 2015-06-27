package nlp.ngram

import files.DataContainer
import files.structure.DataStructure

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

case class BigramPair(w1: Option[String], w2: Option[String])
case class TrigramPair(w0: Option[String], w1: Option[String], w2: Option[String])

