package experiments.emnlp2015

import files.DataContainer
import files.filetypes.input.{CSV, Tab}
import files.structure.DataStructure
import nlp.future.FutureRules._
import nlp.preprocess.tokenization.Tokenizer
import nlp.preprocess.tokenization.impl.Stanford
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 6/7/2015
 */
object Stream {

//  val struct = new DataStructure(idColumnWithName = "state", targetColumnWithName = "sentence") with BasicNLP
//  val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsTokenizedCleanMispelledReplaced.csv", header = true) with CSV
//  val sentiment = new Sentiment(data, struct)
//  sentiment.saveSentimentMatching("E:\\Allen\\R\\emnlp2015\\twitterSentimentNoMispell.csv")

//  runFuture()

//  tokenize()

  def runFuture(): Unit = {
//    val struct = new DataStructure(idColumnWithName = "state", targetColumnWithName = "sentence") with BasicNLP
//    val struct = new DataStructure(idColumn = 1, targetColumn = 2, keepColumns = Vector(3,4,5,6,7,8)) with BasicNLP
//    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\twitterSentiment.csv", header = false) with CSV

//    val struct = new DataStructure(idColumnWithName = "Label", targetColumnWithName = "parsed") with BasicNLP
//    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\fixedOpenClassifier\\MTurkSentences.csv", header = true) with CSV
//    val future = new FutureOnlyTregex(data, struct, (patternFuture2 ++ patternPresent ++ patternsPast).toIterator)
//    future.saveFutureMatching("E:\\Allen\\R\\emnlp2015\\fixedOpenClassifier\\MTurkSentencesFutureRule2.0.csv")

    //=========== read in topic files  ========= //
//    val struct = new DataStructure(targetColumn = 0) with BasicNLP
//    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\fixedOpenClassifier\\topic10_sentences_274.txt", header = false) with Tab
//    val future = new Future(data, struct, (patternFuture2 ++ patternPresent ++ patternsPast).toIterator)
//    future.saveFutureMatching("E:\\Allen\\R\\emnlp2015\\fixedOpenClassifier\\topic10_futureRules2.0.csv")

    // ========== Match NYT times tregex =========== //
//    val struct = new DataStructure(idColumnWithName = "SentenceID", targetColumnWithName = "Parse", keepColumnsWithNames = Vector("ParagraphID", "PageID"))
//    val data = new DataContainer("E:\\Allen\\NYTFuture\\NYT", header = true) with Tab
//    val future = new FutureOnlyTregex(data, struct, futureRulesComplete ++ patternPresent ++ patternsPast,  tcdoc = "E:\\Allen\\R\\emnlp2015\\TCTerms.txt")
//    future.saveFutureMatching("E:\\Allen\\NYTFuture\\NYT_result_2.2\\nyt_by_sen2_2.txt")

    // =========== Match New MTurk result =========== //
//      val struct = new DataStructure(idColumnWithName = "id", targetColumnWithName = "sentence") with BasicNLP
//      val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\mTurk61115_mindwanderingTokenized.csv", header = true) with CSV
//      val future = new Future(data, struct, patternFuture2_1 ++ patternPresent ++ patternsPast)
//      future.saveFutureMatching("E:\\Allen\\R\\emnlp2015\\mTurk61115_mindwanderingonlyFutureRule2_1.csv")

    // =========== 1000 Tweets ========= //
//          val struct = new DataStructure(targetColumnWithName = "Sentence", keepColumnsWithNames = Vector("Allen_fixedOpen", "Tim_fixedOpen")) with BasicNLP
//          val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\RatingOnTweetsFixedOpen.csv", header = true) with CSV
//          val future = new Future(data, struct, patternFuture2_1 ++ patternPresent ++ patternsPast)
//          future.saveFutureMatching("E:\\Allen\\R\\emnlp2015\\training\\RatingOnTweetsFixedOpenRule2_1.csv")

    // =========== MTurk Data Fixed Vs. Open ======== //
//          val struct = new DataStructure(targetColumnWithName = "sentence", keepColumnsWithNames = Vector("Allen", "Tim")) with BasicNLP
//          val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\AllenFixedVsOpen.csv", header = true) with CSV
//          val future = new Future(data, struct, futureRulesComplete ++ patternPresent ++ patternsPast, tcdoc = "E:\\Allen\\R\\emnlp2015\\TCTerms.txt")
//          future.saveFutureMatching("E:\\Allen\\R\\emnlp2015\\training\\AllenFixedVsOpenFutureRule2_2.csv")

    // =========== 4M Tweets (with mispelling) ========= //


    // =========== 4M Tweets ========== //
//        val struct = new DataStructure(idColumn = 1, targetColumn = 2, keepColumns = Vector(3,4,5,6,7,8)) with BasicNLP
//        val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\twitterSentiment.csv", header = false) with CSV
//        val future = new FutureOnlyTregex(data, struct, futureRulesComplete ++ patternPresent ++ patternsPast)
//        future.saveFutureMatching("E:\\Allen\\R\\emnlp2015\\fixedOpenClassifier\\MTurkSentencesFutureRule2.2.csv")

    // ======= 2007 original MTurk data ======== //
//    val struct = new DataStructure(targetColumnWithName = "parsed", keepColumnsWithNames = Vector("Label")) with BasicNLP
//    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\fixedOpenClassifier\\MTurkSentences.csv", header = true) with CSV
//    val future = new FutureOnlyTregex(data, struct, patternFuture ++ patternPresent ++ patternsPast)
//    future.saveFutureMatching("E:\\Allen\\R\\emnlp2015\\training\\MTurkSentences.csv")

    // ======= reparse PCFG on NYT_sample_2.2
//    val data = new DataContainer("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\1000sentenceSampleFromNYT.csv", header = true) with CSV
//    val struct =  new DataStructure(idColumnWithName = "SentenceID", targetColumnWithName = "Sentence")
//    val future = new FutureParse(data, struct)
//    future.saveFutureMatching("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\parsed1000.csv")

    // ======= match NYT_sample_2.2ctn_reduced with rules2.2.2
//    val struct = new DataStructure(idColumnWithName = "SentenceID", targetColumnWithName = "Sentence")
//    val data = new DataContainer("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\1000sentenceSampleFromNYT.csv", header = true) with CSV
//    val future = new Future(data, struct, futureRulesComplete ++ patternsPast, tcdoc = "E:\\Allen\\R\\emnlp2015\\TCTermsReduced.txt", tndoc = "E:\\Allen\\R\\emnlp2015\\TNTermsReduced.txt")
//    future.saveFutureMatching("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\1000sentenceMatched2_2_21.csv")

    // ======= Parse and match Robert's Delayed Discounting data with rules2.2.2
//    val struct = new DataStructure(idColumnWithName = "ID", targetColumnWithName = "Sentence")
//    val data = new DataContainer("E:\\Allen\\Future\\LinguisticslogdataFutureTokenized.csv", header = true) with CSV
//    val future = new Future(data, struct, futureRulesComplete ++ patternsPast, tcdoc = "E:\\Allen\\R\\emnlp2015\\TCTermsReduced.txt",
//                            tndoc = "E:\\Allen\\R\\emnlp2015\\TNTermsReduced.txt")
//    future.saveFutureMatching("E:\\Allen\\Future\\LinguisticslogdataFuture2_2_21.csv")

    // ======= Parse and match Jason's MTurk data with rules2.2.2
    val struct = new DataStructure(idColumnWithName = "id", targetColumnWithName = "sentence")
    val data = new DataContainer("E:\\Allen\\Future\\mTurk61115_mindwanderingonlyTokenized.csv", header = true) with CSV
//    val future = new Future(data, struct, futureRulesComplete ++ patternsPast, tcdoc = "E:\\Allen\\R\\emnlp2015\\TCTermsReduced.txt",
//                            tndoc = "E:\\Allen\\R\\emnlp2015\\TNTermsReduced.txt")
    val future = new Future(data, struct, patternFuture ++ patternsPast)
    future.saveFutureMatching("E:\\Allen\\Future\\mTurk61115_mindwanderingonlyFutureOld.csv")

    // ======== Parse 1000 Tweets for Gabby with rules2.2.2
//      val struct = new DataStructure(targetColumnWithName = "Sentence", idColumnWithName = "ID")
//      val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\1000Tweets.csv", header = true) with CSV
//      val future = new Future(data,struct, futureRulesComplete ++ patternsPast, tcdoc = "E:\\Allen\\R\\emnlp2015\\TCTermsReduced.txt",
//              tndoc = "E:\\Allen\\R\\emnlp2015\\TNTermsReduced.txt")
//      future.saveFutureMatching("E:\\Allen\\Future\\1000tweets2_2_2.csv")
  }

  def tokenize(): Unit = {
//      val struct = new DataStructure(targetColumnWithName = "MindWandering", keepColumnsWithNames = Vector("id"))
//      val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\mTurk61115_mindwanderingonly.csv", header = true) with CSV
//      val tokenizer = new Tokenizer(data, struct) with Stanford
//      tokenizer.tokenize().exec("E:\\Allen\\R\\emnlp2015\\mTurk61115_mindwanderingTokenized.csv", fileAppend = true)

    // ======= Tokenize Robert's Delayed Discounting data
//    val struct = new DataStructure(targetColumnWithName = "Sentence", keepColumnsWithNames = Vector("ID"))
//    val data = new DataContainer("E:\\Allen\\Future\\LinguisticslogdataFuture.csv", header = true) with CSV
//    val tokenizer = new Tokenizer(data, struct) with Stanford
//    tokenizer.tokenize().exec("E:\\Allen\\Future\\LinguisticslogdataFutureTokenized.csv", fileAppend = true)

    // ======= Tokenize Jason's MTurk data
    val struct = new DataStructure(targetColumnWithName = "MindWandering", keepColumnsWithNames = Vector("id"))
    val data = new DataContainer("E:\\Allen\\Future\\mTurk61115_mindwanderingonly.csv", header = true) with CSV
    val tokenizer = new Tokenizer(data, struct) with Stanford
    tokenizer.tokenize().exec("E:\\Allen\\Future\\mTurk61115_mindwanderingonlyTokenized.csv", fileAppend = true)
  }

  //load future rules from file
//  def futureRulesComplete: List[String] = scala.io.Source.fromFile("E:\\Allen\\R\\emnlp2015\\theRules.txt").getLines().toList
  def futureRulesComplete: List[String] = scala.io.Source.fromFile("E:\\Allen\\NYTFuture\\Rules2_2_2.txt").getLines().toList

}
