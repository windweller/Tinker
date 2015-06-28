package tweets

import files.DataContainer
import files.filetypes.format._
import utils.ParameterCallToOption.Implicits._
import org.scalatest.FlatSpec
import scala.collection.JavaConversions._
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by anie on 6/27/2015.
 */
class GroupingTweets extends FlatSpec {
  "fuzzyMatch matcher" should "group tweets" in {
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\Tweets", header = true, fuzzyMatch = 16) with CSV
    data.dataIterators.foreach(e => println(e._1))
  }

  "change file name" should "work" in {
    val file = new java.io.File("E:\\Allen\\R\\emnlp2015\\Tweets")
    file.listFiles().foreach(e => if (e.getName.length <= 16) Files.move(e.toPath, e.toPath.resolveSibling(e.getName.split("\\.")(0) + "2015_04_16.txt")) )
//    file.listFiles().foreach(e => if (e.getName.length <= 16) println(e.getName.split("\\.")(0) + "2015_04_16.txt"))
  }
}
