package tweets

import com.github.tototoshi.csv.CSVWriter
import newFiles.DataContainer
import newFiles.filetypes.csv.CSV
import nlp.ngram.{Unigram, Ngram}
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.implicits._

/**
 * Created by Aimingnie on 4/23/15
 */
class RunAllTweets extends FlatSpec {
  "Combine tweets" should "work" in {
    val data = new DataContainer("/Users/Aimingnie/Desktop/R/ACL2015/Tweets/", header = true, fuzzyMatch = 9) with CSV
    val ngram = new Ngram(data) with Unigram
    val result = ngram.getTokenNumber
    val output: CSVWriter = CSVWriter.open("/Users/Aimingnie/Desktop/R/ACL2015/unigramToken.txt", append = true)
    println(result.size)
    output.writeRow(result.toSeq)
  }
}
