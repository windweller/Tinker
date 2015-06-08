package experiments.emnlp2015

import newFiles.DataContainer
import newFiles.filetypes.tab.Tab
import newFiles.structure.DataStructure
import newFiles.structure.predefined.BasicNLP
import nlp.future.Future
import nlp.future.FutureRules._
import utils.ParameterCallToOption.implicits._


/**
 * Created by anie on 6/7/2015.
 */
object Stream extends App {

  val struct = new DataStructure(idColumnWithName = "state", targetColumnWithName = "sentence") with BasicNLP
  val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsTokenizedClean2.csv", header = true) with Tab
  val sentiment = new Sentiment(data, struct)
  sentiment.saveSentimentMatching("E:\\Allen\\R\\emnlp2015\\twitterSentiment.csv")

  def runFuture(): Unit = {
    val struct = new DataStructure(idColumnWithName = "state", targetColumnWithName = "sentence") with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsTokenizedClean2.csv", header = true) with Tab
    val future = new Future(data, struct, (patternFuture ++ patternPresent ++ patternsPast).toIterator)
    future.saveFutureMatching("E:\\Allen\\R\\emnlp2015\\twitterFuture.csv")
  }

}
