package tweets

import java.io.File

import com.github.tototoshi.csv.CSVWriter
import org.scalatest.FlatSpec

/**
 * Created by anie on 6/14/2015.
 */
class MergingTweets extends FlatSpec {

  "print out file name" should "work" in {
    val dir = new File("E:\\Allen\\R\\emnlp2015\\tweets-merge\\tweets-old")
    val files = dir.listFiles()
    files.foreach(f => print(f.getName.replace("twitterFutureRuleOld.csv.", "").replace(".merge", "") + "\r\n"))
  }

  "merge old tweets by pos vs. neg" should "work" in {

    val output: CSVWriter = CSVWriter.open("E:\\Allen\\R\\emnlp2015\\tweets_future_pos_neg_count.csv", append = true)

    output.writeRow(Seq("exposfuture", "posfuture", "neutralfuture", "negfuture", "exnegfuture", "nanfuture"))

    val dir = new File("E:\\Allen\\R\\emnlp2015\\tweets-merge\\tweets-old")
    val files = dir.listFiles()

    //create 5 labels associating future with emotions
    files.foreach { file => //every file is a state
      val body = scala.io.Source.fromFile(file).getLines()
      var nanfuture = 0
      var neutralFuture = 0
      var exposfuture = 0
      var posfuture = 0
      var negfuture = 0
      var exnegfuture = 0

      body.foreach { line =>
        val cells = line.split("\t")
        if (cells(1) == "2") {
          nanfuture += 1
        } //after this cells(1) is always 1
        else if (cells(2) == "Negative") {
          negfuture += 1
        }
        else if (cells(2) == "Very negative") {
          exnegfuture += 1
        }
        else if (cells(2) == "Neutral") {
          neutralFuture += 1
        }
        else if (cells(2) == "Positive") {
          posfuture += 1
        }
        else if (cells(2) == "Very positive") {
          exposfuture += 1
        }
      }

      //then we print them out
      output.writeRow(Seq(exposfuture, posfuture, neutralFuture, negfuture, exnegfuture, nanfuture))
    }
  }

  "merge new tweets by pos vs. neg" should "work" in {
    val output: CSVWriter = CSVWriter.open("E:\\Allen\\R\\emnlp2015\\tweets_future_fixedOpen_pos_neg_count.csv", append = true)

    output.writeRow(Seq("expos_open_future", "pos_open_future", "neutral_open_future", "neg_open_future", "exneg_open_future",
                        "expos_fixed_future", "pos_fixed_future", "neutral_fixed_future", "neg_fixed_future", "exneg_fixed_future"))

    val dir = new File("E:\\Allen\\R\\emnlp2015\\tweets-merge\\tweets-new")
    val files = dir.listFiles()

    files.foreach { file => //every file is a state
      val body = scala.io.Source.fromFile(file).getLines()

      var expos_open_future = 0
      var pos_open_future = 0
      var neutral_open_future = 0
      var neg_open_future = 0
      var exneg_open_future = 0

      var expos_fixed_future = 0
      var pos_fixed_future = 0
      var neutral_fixed_future = 0
      var neg_fixed_future = 0
      var exneg_fixed_future = 0

      var nanfuture = 0

      body.foreach { line =>
        val cells = line.split("\t")
        if (cells(1) == "2") {
          nanfuture += 1
        } //after this cells(1) is always 1
        else if (cells(1) == "0") {
          //open future
          if (cells(2) == "Very positive") {
            expos_open_future += 1
          }
          else if (cells(2) == "Positive") {
            pos_open_future += 1
          }
          else if (cells(2) == "Neutral") {
            neutral_open_future += 1
          }
          else if (cells(2) == "Negative") {
            neg_open_future += 1
          }
          else if (cells(2) == "Very negative") {
            exneg_open_future += 1
          }

        }
        else if (cells(1) == "1") {
          //fixed future
          if (cells(2) == "Very positive") {
            expos_fixed_future += 1
          }
          else if (cells(2) == "Positive") {
            pos_fixed_future += 1
          }
          else if (cells(2) == "Neutral") {
            neutral_open_future += 1
          }
          else if (cells(2) == "Negative") {
            neg_open_future += 1
          }
          else if (cells(2) == "Very negative") {
            exneg_open_future += 1
          }

        }
      }

      //then we print them out
      output.writeRow(Seq(expos_open_future, pos_open_future, neutral_open_future, neg_open_future, exneg_open_future,
        expos_fixed_future, pos_fixed_future, neutral_fixed_future, neg_fixed_future, exneg_fixed_future, nanfuture))
    }

  }


}
