package tweets

import com.github.tototoshi.csv.CSVWriter
import files.DataContainer
import files.filetypes.input.{Tab, CSV}
import files.operations.FileOp
import files.structure.DataStructure
import nlp.basic.Sentence
import nlp.basic.sentence.Split
import nlp.preprocess.filters._
import nlp.future.Future
import nlp.ngram.{Unigram, Ngram}
import nlp.sentiment.Sentiment
//import nlp.sentiment.impl.Stanford
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.Implicits._

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

//  "count sentence #" should "work" in {
//    val struct = new DataStructure(targetColumnWithName = "MWand1") 
//    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/jason/", header = true) with CSV
//    val basic = new Sentence(data, struct) with Split
//    val result = basic.countSentences()
//    val output: CSVWriter = CSVWriter.open("/Users/Aimingnie/Desktop/R/ACL2015/sentenceCount.txt", append = true)
//    output.writeRow(result.toSeq)
//  }

  "filtering Twitter with Emoticon" should "work" in {
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/Tweets/", header = true, fuzzyMatch = 9) with CSV
    val struct = new DataStructure(targetColumnWithName = "Text") 

    val filter = new Filter(data, struct) with TwitterFilter
    filter.preprocess("/Users/Aimingnie/Desktop/R/ACL2015/tweetsByState.csv")
  }

  "further filter Tweets with Character" should "work" in {
    val struct = new DataStructure(idColumn = 0, targetColumn = 1) 
    val data = new DataContainer("E:\\Allen\\R\\acl2015\\tweetsByStateSplitted.csv", header = false) with CSV

    val filter = new Filter(data, struct) with CharacterFilter
    filter.preprocess("E:\\Allen\\R\\acl2015\\tweetsByStateSplittedCleaned.csv")
  }

//  "split tweets" should "work" in {
//    val struct = new DataStructure(idColumn = 0, targetColumn = 1) 
//    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/tweetsByState.csv", header = false) with CSV
//    val basic = new Sentence(data, struct) with Split
//    basic.splitSentences("/Users/Aimingnie/Desktop/R/ACL2015/tweetsByStateSplitted.csv")
//  }

//  "sentiment analysis" should "work"  in {
//    val struct = new DataStructure(idColumn = 0, targetColumn = 2)
//    val data = new DataContainer("E:\\Allen\\R\\acl2015\\tweetsByStateSplittedCleaned.tab", header = false) with Tab
//    val sentiment = new Sentiment(data, struct) with Stanford
//    sentiment.classify("E:\\Allen\\R\\acl2015\\twitterSentiment2.csv")
//  }

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