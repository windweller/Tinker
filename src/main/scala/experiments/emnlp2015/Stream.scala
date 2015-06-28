package experiments.emnlp2015

import files.DataContainer
import files.filetypes.format.{Tab, CSV}
import files.structure.DataStructure
import files.structure.predefined.BasicNLP
import nlp.future.FutureRules._
import nlp.preprocess.tokenization.Tokenizer
import nlp.preprocess.tokenization.impl.Stanford
import utils.ParameterCallToOption.implicits._

/**
 * Created by anie on 6/7/2015
 */
object Stream extends App {

//  val struct = new DataStructure(idColumnWithName = "state", targetColumnWithName = "sentence") with BasicNLP
//  val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsTokenizedCleanMispelledReplaced.csv", header = true) with CSV
//  val sentiment = new Sentiment(data, struct)
//  sentiment.saveSentimentMatching("E:\\Allen\\R\\emnlp2015\\twitterSentimentNoMispell.csv")

  runFuture()

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
    val struct = new DataStructure(idColumnWithName = "SentenceID", targetColumnWithName = "Parse", keepColumnsWithNames = Vector("ParagraphID", "PageID")) with BasicNLP
    val data = new DataContainer("E:\\Allen\\NYTFuture\\NYT", header = true) with Tab
    val future = new FutureOnlyTregex(data, struct, futureRulesComplete ++ patternPresent ++ patternsPast,  tcdoc = "E:\\Allen\\R\\emnlp2015\\TCTerms.txt")
    future.saveFutureMatching("E:\\Allen\\NYTFuture\\NYT_result_2.2\\nyt_by_sen2_2.txt")

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
  }

  def tokenize(): Unit = {
      val struct = new DataStructure(targetColumnWithName = "MindWandering", keepColumnsWithNames = Vector("id")) with BasicNLP
      val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\mTurk61115_mindwanderingonly.csv", header = true) with CSV
      val tokenizer = new Tokenizer(data, struct) with Stanford
      tokenizer.tokenize().exec("E:\\Allen\\R\\emnlp2015\\mTurk61115_mindwanderingTokenized.csv", fileAppend = true)
  }

  //load future rules from file
  def futureRulesComplete: List[String] = io.Source.fromFile("E:\\Allen\\R\\emnlp2015\\theRules.txt").getLines().toList

}
