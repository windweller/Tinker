package nlp.future

import newFiles.DataContainer
import newFiles.filetypes.tab.Tab
import newFiles.structure.DataStructure
import newFiles.structure.predefined.BasicNLP
import utils.ParameterCallToOption.implicits._
import FutureRules._


/**
 * Created by anie on 4/26/2015.
 */
object FutureOnTweets extends App {

  val struct = new DataStructure(idColumn = 0, targetColumn = 2) with BasicNLP
  val data = new DataContainer("E:\\Allen\\R\\acl2015\\tweetsByStateSplittedCleaned2.tab", header = false) with Tab
  val patternRaw = io.Source.fromFile("E:\\Allen\\R\\acl2015\\FutureRules_2.0b.txt").getLines().filter(e => e != "").map(e => e.replaceFirst("\\s+$", ""))
  val future = new Future(data, struct, patternRaw)
  future.saveFutureMatching("E:\\Allen\\R\\acl2015\\twitterFutureNewRules.csv")


  def runOldRules(): Unit = {
    val struct = new DataStructure(idColumn = 0, targetColumn = 2) with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\acl2015\\tweetsByStateSplittedCleaned2.tab", header = false) with Tab
    val future = new Future(data, struct, (patternFuture ++ patternPresent ++ patternsPast).toIterator)
    future.saveFutureMatching("E:\\Allen\\R\\acl2015\\twitterFuture2.csv")
  }

}
