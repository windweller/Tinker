package emnlp2015

import java.io.{FileOutputStream, OutputStreamWriter, PrintWriter}

import core.DataContainer
import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer
import files.filetypes.input.Tab
import core.structure.DataStructure
import utils.ParameterCallToOption.Implicits._

import scala.collection.mutable

/**
 * Created by anie on 5/24/2015.
 */
object WeightedVector extends App {

  val struct = new DataStructure(targetColumn = 1, keepColumns = Vector(0))
  val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsTokenizedClean.csv", header = true) with Tab

  val tokenizer = new EnglishTokenizer

  //Some preparations
  import nlp.ngram._
  val printer: PrintWriter = new PrintWriter(new OutputStreamWriter(
    new FileOutputStream("E:\\Allen\\R\\emnlp2015\\uni_bi_trigram_vector_by_state.txt",true), "UTF-8"))

  //1st Traversal: dictionary construction

  //1. Construct a HashMap [token -> Number]
  val dictionaryUni = mutable.HashMap.empty[String, Int]
  val dictionaryBi = mutable.HashMap.empty[BigramPair, Int]
  val dictionaryTri = mutable.HashMap.empty[TrigramPair, Int]

  val it = data.strippedData //no need for file info

  //1.5 We construct a HashMap for State-level
  val stateFeatureVector = mutable.HashMap.empty[Int, Int]
  var previousState = ""

  //2. Go through all the rows to construct this HashMap
  //no need to know the state info

  while (it.hasNext) {
    //sentence level

    val row = it.next()
    //for every sentence, extract uni, bi, trigram features
    //ClearNLP comes in to break down sentences
    val tokens = tokenizer.tokenize(row("sentence"))
    val tokenit = tokens.iterator()
    val sentenceFeatureVector = mutable.HashMap.empty[Int, Int]

    var previousWord: Option[String] = None
    var nextWord: Option[String] = None

    //initialize previous state
    if (previousState == "") previousState = row("state")
    //state vector is collected until a new state is encountered
    //we print out hashMap, and clear it
    if (previousState != row("state")) {
      printer.print(previousState + " ")
      printer.println(stateFeatureVector.map(e => e._1 + ":" + e._2).mkString(" "))
      previousState = row("state")
      stateFeatureVector.clear()
    }

    while (tokenit.hasNext) {
      //word level

      val w0 = previousWord
      val w1 = if (nextWord == None) Some(tokenit.next()) else nextWord
      previousWord = w1

      val w2 = if (tokenit.hasNext) {
        nextWord = Some(tokenit.next())
        nextWord
      } else None

      //NOTICE: feature index started at 0
      //f1, f2, f3 will be None if new, or return its pos

      var pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r1 = dictionaryUni.get(w1.get)
      if (r1.isEmpty) {
        val f1 = dictionaryUni.put(w1.get, dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
        updateFeatureVector(pos, stateFeatureVector)
      }
      else {updateFeatureVector(r1.get, sentenceFeatureVector); updateFeatureVector(r1.get, stateFeatureVector)}


      pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r2 = dictionaryBi.get(BigramPair(w1, w2))
      if (r2.isEmpty) {
        val f2 = dictionaryBi.put(BigramPair(w1, w2), dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
        updateFeatureVector(pos, stateFeatureVector)
      }
      else {updateFeatureVector(r2.get, sentenceFeatureVector); updateFeatureVector(r2.get, stateFeatureVector)}


      pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r3 = dictionaryTri.get(TrigramPair(w0, w1, w2))
      if (r3.isEmpty) {
        val f3 = dictionaryTri.put(TrigramPair(w0, w1, w2), dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
        updateFeatureVector(pos, stateFeatureVector)
      }
      else {updateFeatureVector(r3.get, sentenceFeatureVector); updateFeatureVector(r3.get, stateFeatureVector)}

    }
    //when end
    if (nextWord.nonEmpty) {
      var pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r6 = dictionaryUni.get(nextWord.get)
      if (r6.isEmpty) {
        val f6 = dictionaryUni.put(nextWord.get, dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
        updateFeatureVector(pos, stateFeatureVector)
      }
      else {updateFeatureVector(r6.get, sentenceFeatureVector); updateFeatureVector(r6.get, stateFeatureVector)}


      pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r4 = dictionaryBi.get(BigramPair(nextWord, None))
      if (r4.isEmpty) {
        val f4 = dictionaryBi.put(BigramPair(nextWord, None), dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
        updateFeatureVector(pos, stateFeatureVector)
      }
      else {updateFeatureVector(r4.get, sentenceFeatureVector); updateFeatureVector(r4.get, stateFeatureVector)}


      pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r5 = dictionaryTri.get(TrigramPair(previousWord, nextWord, None))
      if (r5.isEmpty) {
        val f5 = dictionaryTri.put(TrigramPair(previousWord, nextWord, None), dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
        updateFeatureVector(pos, stateFeatureVector)
      } else {updateFeatureVector(r5.get, sentenceFeatureVector); updateFeatureVector(r5.get, stateFeatureVector)}
    }

    //maybe print it out???
    /* This is when feature extraction is done, every sentence is turned into a vector */

    //sentence-level printout
//    printer.print(row("state") + " ")
//    printer.println(sentenceFeatureVector.map(e => e._1 + ":" + e._2).mkString(" "))

  }


  //then after all sentences are done, print out last state
  printer.print(previousState + " ")
  printer.println(stateFeatureVector.map(e => e._1 + ":" + e._2).mkString(" "))

  //don't forget to close printer after all traversal
  printer.close()

  def updateFeatureVector(pos: Int, sentenceFeatureVector: mutable.HashMap[Int, Int]): Unit = {
    val check = sentenceFeatureVector.get(pos)
    if (check.isEmpty) {
      //meaning this is new
      sentenceFeatureVector.put(pos, 1)
    }
    else {
      sentenceFeatureVector.update(pos, check.get + 1)
    }
  }


}
