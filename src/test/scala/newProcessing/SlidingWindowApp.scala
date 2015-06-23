package newProcessing

import newFiles.DataContainer
import newFiles.filetypes.csv.CSV
import newFiles.operations.FileOp
import utils.ParameterCallToOption.implicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Success, Failure}

/**
 * Created by anie on 5/19/2015.
 */
object SlidingWindowApp extends App {

//  val i2 = run(2)

//  (2 to 7).par.foreach { i =>
//    run(i)
//    println(i + "th run is finished")
//  }

  def run(i: Int): Unit = {
    val dc = new DataContainer(f = "E:\\Allen\\NYTFuture\\NYT_result_2.1\\nyt_by_sen2_1.txt", header = true) with CSV with FileOp
    dc.compressBySlidingWindow(discardColsWithName = Vector("id", "ParagraphID", "PageID", "Parse", "SentenceID"), windowSize = i)
      .exec(filePath = "E:\\Allen\\NYTFuture\\NYT_result_2.1\\NYT_slidingWindow_size" + i + ".txt")
  }

  //compress by paragraph
  val dc = new DataContainer(f = "E:\\Allen\\NYTFuture\\NYT_result_2.1\\nyt_by_sen2_1.txt", header = true) with CSV with FileOp
  dc.compressBySum(groupByColWithName = "ParagraphID", discardColsWithName = Vector("id", "PageID", "Parse", "SentenceID"))
    .exec(filePath = "E:\\Allen\\NYTFuture\\NYT_result_2.1\\NYT_byParagraph.txt")
}
