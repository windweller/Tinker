package tweets

import java.io.File

import com.github.tototoshi.csv.CSVWriter
import core.DataContainer
import files.filetypes.input.{Tab, CSV}
import files.operations.FileOp
import core.structure.DataStructure
import nlp.basic.Sentence
import nlp.basic.sentence.Split
import nlp.preprocess.filters._
import nlp.future.Future
import nlp.ngram.{Unigram, Ngram}
import nlp.sentiment.Sentiment
import nlp.sentiment.impl.Stanford
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.Implicits._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Aimingnie on 4/23/15
 */
class RunAllTweets extends FlatSpec {

  "Combine tweets" should "work" in {
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/Tweets/", header = true, fuzzyMatch = 9) with CSV
    val struct = new DataStructure(targetColumnWithName = "Text") 
    val ngram = new Ngram(data, struct) with Unigram
    val result = ngram.getUnigramTokenNumber
    val output: CSVWriter = CSVWriter.open("/Users/Aimingnie/Desktop/R/ACL2015/unigramToken.txt", append = true)
    println(result.size)
    output.writeRow(result.toSeq)
  }

  "count groups" should "work" in {
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/Tweets/", header = true, fuzzyMatch = 9) with CSV
    println(data.data.length)
  }

  "count sentence #" should "work" in {
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsByStateFinalCleaned.csv", header = true) with CSV
    var z = 0
    data.data.foreach(e => {z += 1; if (z % 10000 == 0) println(z)})
    println(z)
  }

  "filtering Twitter with Emoticon" should "work" in {
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsByCountryCharacterFiltered.txt", header = true) with Tab
    val struct = new DataStructure(targetColumnWithName = "Tweet", idColumnWithName = "State")

    val filter = new Filter(data, struct) with TwitterFilter
    filter.preprocess("E:\\Allen\\R\\emnlp2015\\tweetsByCountryCharacterFilteredEmoticonRemoved.csv")
  }

  "further filter Tweets with Character" should "work" in {
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\mergedCountryTweetsTab.txt", header = true) with Tab
    val struct = new DataStructure(targetColumnWithName = "Tweet", idColumnWithName = "State")
    val filter = new Filter(data, struct) with CharacterFilter
    filter.preprocess("E:\\Allen\\R\\emnlp2015\\tweetsByCountryCharacterFiltered.txt")
  }

//  "split tweets" should "work" in {
//    val struct = new DataStructure(idColumn = 0, targetColumn = 1) 
//    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/tweetsByState.csv", header = false) with CSV
//    val basic = new Sentence(data, struct) with Split
//    basic.splitSentences("/Users/Aimingnie/Desktop/R/ACL2015/tweetsByStateSplitted.csv")
//  }

  "sentiment analysis" should "work"  in {
    val struct = new DataStructure(idColumn = 0, targetColumn = 2) 
    val data = new DataContainer("E:\\Allen\\R\\acl2015\\tweetsByStateSplittedCleaned.tab", header = false) with Tab
    val sentiment = new Sentiment(data, struct) with Stanford
    sentiment.classify("E:\\Allen\\R\\acl2015\\twitterSentiment2.csv")
  }

  "average by group" should "work" in {
    val struct = new DataStructure(idColumn = 0, ignoreColumn = 1) 
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/twitterSentiment2.csv", header = false) with CSV with FileOp
    data.averageByGroup("/Users/Aimingnie/Desktop/R/ACL2015/twitterSentimentAvg.csv", struct)
  }

  "average by future" should "work" in {
    val struct = new DataStructure(idColumn = 0, ignoreColumn = 1) 
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/twitterFuture2.csv", header = false) with CSV with FileOp
    data.averageByGroup("/Users/Aimingnie/Desktop/R/ACL2015/futureAvg.csv", struct)
  }

}