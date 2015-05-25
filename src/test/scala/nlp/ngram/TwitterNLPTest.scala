package nlp.ngram

import com.github.tototoshi.csv.CSVWriter
import newFiles.DataContainer
import newFiles.filetypes.csv.CSV
import newFiles.structure.DataStructure
import newFiles.structure.predefined.BasicNLP
import nlp.basic.Sentence
import nlp.basic.sentence.Split
import nlp.preprocess.filters.{TwitterFilter, Filter}
import nlp.preprocess.tokenization.Tokenizer
import nlp.preprocess.tokenization.impl.{ClearNLP, Stanford}
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.implicits._

/**
 * Created by anie on 5/22/2015.
 */
class TwitterNLPTest extends FlatSpec {

  "count sentence #" should "work" in {
    val struct = new DataStructure(targetColumnWithName = "Text") with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\Tweets\\", header = true, fuzzyMatch = 9) with CSV
    println(data.strip.length)
  }

  "clean up tweets" should "work" in {
    val struct = new DataStructure(targetColumnWithName = "Text") with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\Tweets\\", header = true, fuzzyMatch = 9) with CSV
    val filter = new Filter(data, struct) with TwitterFilter
    filter.preprocess("E:\\Allen\\R\\emnlp2015\\tweetsCleaned.csv")
  }

  "tokenize" should "work" in {
    val struct = new DataStructure(targetColumn = 1, keepColumns = Vector(0)) with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsCleaned.csv", header = false) with CSV
    val tokenizer = new Tokenizer(data, struct) with Stanford
    tokenizer.tokenize().exec("E:\\Allen\\R\\emnlp2015\\tweetsTokenized.csv", fileAppend = true)
  }

  "unigram/vocabulary count by state" should "work" in {
    val struct = new DataStructure(targetColumnWithName = "Text") with BasicNLP
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\Tweets\\", header = true, fuzzyMatch = 9) with CSV
    val ngram = new Ngram(data, struct) with Unigram

    val result = ngram.getUnigramTokenNumber
    val output: CSVWriter = CSVWriter.open("E:\\Allen\\R\\emnlp2015\\unigramToken.txt", append = true)
    output.writeRow(result.toSeq)
  }

}
