package nlp.sentiment

import newFiles.DataContainer
import newFiles.structure.DataStructure

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Aimingnie on 4/25/15
 *
 * Base model for sentiment analysis
 * can be swapped with different implementations
 */
abstract class Sentiment(val data: DataContainer, val struct: DataStructure) {

  //probably not going to be [String, Int]
  def classify(saveLoc: String): Unit

}