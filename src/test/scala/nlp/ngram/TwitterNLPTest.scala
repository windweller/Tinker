package nlp.ngram

import java.io.{FileOutputStream, OutputStreamWriter, PrintWriter}

import com.github.tototoshi.csv.CSVWriter
import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer
import newFiles.DataContainer
import newFiles.RowTypes.NormalRow
import newFiles.filetypes.csv.CSV
import newFiles.filetypes.tab.{Tab, TabOutput}
import newFiles.operations.FileOp
import newFiles.structure.DataStructure
import newFiles.structure.predefined.BasicNLP
import newProcessing.Printer
import newProcessing.buffers.BufferConfig
import nlp.basic.Sentence
import nlp.basic.sentence.Split
import nlp.preprocess.filters.{CharacterFilter, TwitterFilter, Filter}
import nlp.preprocess.tokenization.Tokenizer
import nlp.preprocess.tokenization.impl.Stanford
import nlp.preprocess.tokenization.impl.{ClearNLP, Stanford}
import nlp.sentiment.Sentiment
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.implicits._

import scala.collection.mutable

/**
 * Created by anie on 5/22/2015.
 */
class TwitterNLPTest extends FlatSpec {

  "count sentence #" should "work" in {
    val struct = new DataStructure(targetColumnWithName = "Text") with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\Tweets\\", header = true, fuzzyMatch = 9) with CSV
    println(data.strip.length)
  }

  "clean up tweets" should "work" in {
    val struct = new DataStructure(targetColumnWithName = "Text") with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\Tweets\\", header = true, fuzzyMatch = 9) with CSV
    val filter = new Filter(data, struct) with TwitterFilter
    filter.preprocess("E:\\Allen\\R\\emnlp2015\\tweetsCleaned.csv")
  }

  "tokenize" should "work" in {
    val struct = new DataStructure(targetColumn = 1, keepColumns = Vector(0)) with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsCleaned.csv", header = false) with CSV
    val tokenizer = new Tokenizer(data, struct) with Stanford
    tokenizer.tokenize().exec("E:\\Allen\\R\\emnlp2015\\tweetsTokenized.csv", fileAppend = true)
  }

  "unigram/vocabulary count by state" should "work" in {
    val struct = new DataStructure(targetColumnWithName = "Text") with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\Tweets\\", header = true, fuzzyMatch = 9) with CSV
    val ngram = new Ngram(data, struct) with Unigram

    val result = ngram.getUnigramTokenNumber
    val output: CSVWriter = CSVWriter.open("E:\\Allen\\R\\emnlp2015\\unigramToken.txt", append = true)
    output.writeRow(result.toSeq)
  }

  "count tweets by state" should "work" in {
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsTokenized.csv", header = true) with CSV with FileOp
    data.countByGroup(groupColWithName = "state").exec(filePath = "E:\\Allen\\R\\emnlp2015\\tweetsCountByState.txt")
  }

  "clean tokenized tweets" should "work" in {
    val struct = new DataStructure(idColumnWithName = "state", targetColumnWithName = "sentence") with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsTokenized.csv", header = true) with CSV
    val filter = new Filter(data, struct) with CharacterFilter
    filter.preprocess("E:\\Allen\\R\\emnlp2015\\tweetsTokenizedClean.csv")
  }

  "sentiment analysis" should "work" in {
    val struct = new DataStructure(idColumnWithName = "state", targetColumnWithName = "sentence") with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsTokenizedClean.csv", header = true) with Tab
    val sentiment = new Sentiment(data, struct) with nlp.sentiment.impl.Stanford
    sentiment.classify("E:\\Allen\\R\\emnlp2015\\twitterSentiment2.csv")
  }

  "uni-bi-trigram hashmap construction" should "work" in {

    val tokenizer = new EnglishTokenizer

    //Some preparations
    import nlp.ngram._

    //1st Traversal: dictionary construction

    //1. Construct a HashMap [token -> Number]
    val dictionaryUni = mutable.HashMap.empty[String, Int]
    val dictionaryBi = mutable.HashMap.empty[BigramPair, Int]
    val dictionaryTri = mutable.HashMap.empty[TrigramPair, Int]

    val tokens = tokenizer.tokenize("I am here.")
    val tokenit = tokens.iterator()
    val sentenceFeatureVector = mutable.HashMap.empty[Int, Int]

    var previousWord: Option[String] = None
    var nextWord: Option[String] = None

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
      var pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r1 = dictionaryUni.get(w1.get)
      if (r1.isEmpty) {
        val f1 = dictionaryUni.put(w1.get, dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
      }
      else updateFeatureVector(r1.get, sentenceFeatureVector)


      pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r2 = dictionaryBi.get(BigramPair(w1, w2))
      if (r2.isEmpty) {
        val f2 = dictionaryBi.put(BigramPair(w1, w2), dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
      }
      else updateFeatureVector(r2.get, sentenceFeatureVector)


      pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r3 = dictionaryTri.get(TrigramPair(w0, w1, w2))
      if (r3.isEmpty) {
        val f3 = dictionaryTri.put(TrigramPair(w0, w1, w2), dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
      }
      else updateFeatureVector(r3.get, sentenceFeatureVector)

    }
    //when end
    if (nextWord.nonEmpty) {
      var pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size

      val r6 = dictionaryUni.get(nextWord.get)
      if (r6.isEmpty) {
        val f6 = dictionaryUni.put(nextWord.get, dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
      }
      else updateFeatureVector(r6.get, sentenceFeatureVector)

      pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r4 = dictionaryBi.get(BigramPair(nextWord, None))
      if (r4.isEmpty) {
        val f4 = dictionaryBi.put(BigramPair(nextWord, None), dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
      }
      else updateFeatureVector(r4.get, sentenceFeatureVector)


      pos = dictionaryTri.size + dictionaryBi.size + dictionaryUni.size
      val r5 = dictionaryTri.get(TrigramPair(previousWord, nextWord, None))
      if (r5.isEmpty) {
        val f5 = dictionaryTri.put(TrigramPair(previousWord, nextWord, None), dictionaryTri.size + dictionaryBi.size + dictionaryUni.size)
        updateFeatureVector(pos, sentenceFeatureVector)
      } else updateFeatureVector(r5.get, sentenceFeatureVector)
    }


    println(dictionaryUni)
    println(dictionaryBi)
    println(dictionaryTri)
    println(sentenceFeatureVector)

//    val printer: PrintWriter = new PrintWriter(new OutputStreamWriter(
//                                      new FileOutputStream("E:\\Allen\\R\\emnlp2015\\testoutput.txt",true), "UTF-8"))
//
//    printer.print("Tweets_OK" + " ")
//    printer.println(sentenceFeatureVector.map(e => e._1 + ":" + e._2).mkString(" "))
//    printer.close()
  }

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