package tweets

import com.github.tototoshi.csv.CSVWriter
import newFiles.DataContainer
import newFiles.filetypes.csv.CSV
import newFiles.structure.DataStructure
import newFiles.structure.predefined.BasicNLP
import nlp.basic.Sentence
import nlp.basic.sentence.Split
import nlp.filters._
import nlp.ngram.{Unigram, Ngram}
import nlp.sentiment.Sentiment
import nlp.sentiment.implementations.Stanford
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.implicits._

/**
 * Created by Aimingnie on 4/23/15
 */
class RunAllTweets extends FlatSpec {

  "Combine tweets" should "work" in {
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/Tweets/", header = true, fuzzyMatch = 9) with CSV
    val struct = new DataStructure(targetColumnWithName = "Text") with BasicNLP
    val ngram = new Ngram(data, struct) with Unigram
    val result = ngram.getTokenNumber
    val output: CSVWriter = CSVWriter.open("/Users/Aimingnie/Desktop/R/ACL2015/unigramToken.txt", append = true)
    println(result.size)
    output.writeRow(result.toSeq)
  }

  "count sentence #" should "work" in {
    val struct = new DataStructure(targetColumnWithName = "MWand1") with BasicNLP
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/jason/", header = true) with CSV
    val basic = new Sentence(data, struct) with Split
    val result = basic.countSentences()
    val output: CSVWriter = CSVWriter.open("/Users/Aimingnie/Desktop/R/ACL2015/sentenceCount.txt", append = true)
    output.writeRow(result.toSeq)
  }

  "filtering Twitter with Emoticon" should "work" in {
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/Tweets/", header = true, fuzzyMatch = 9) with CSV
    val struct = new DataStructure(targetColumnWithName = "Text") with BasicNLP

    val filter = new Filter(data, struct) with TwitterFilter
    filter.preprocess("/Users/Aimingnie/Desktop/R/ACL2015/tweetsByState.csv")
  }

  "split tweets" should "work" in {
    val struct = new DataStructure(idColumn = 0, targetColumn = 1) with BasicNLP
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/tweetsByState.csv", header = false) with CSV
    val basic = new Sentence(data, struct) with Split
    basic.splitSentences("/Users/Aimingnie/Desktop/R/ACL2015/tweetsByStateSplitted.csv")
  }

  "sentiment analysis" should "work"  in {
    val struct = new DataStructure(idColumn = 0, targetColumn = 1) with BasicNLP
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/tweetsByStateSplitted.csv", header = false) with CSV
    val sentiment = new Sentiment(data, struct) with Stanford
    sentiment.classify("/Users/Aimingnie/Desktop/R/ACL2015/twitterSentiment.csv")
  }

}