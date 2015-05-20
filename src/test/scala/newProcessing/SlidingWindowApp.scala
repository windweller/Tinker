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

  val i2 = run(2)

//  (4 to 9).par.foreach { i =>
//    run(i)
//    println(i + "th run is finished")
//  }

  def run(i: Int): Unit = {
    val dc = new DataContainer(f = "E:\\Allen\\NYTFuture\\NYT_result_2.0b\\nyt_by_sen.txt", header = true) with CSV with FileOp
    dc.compressBySlidingWindow(discardColsWithName = Vector("id", "ParagraphID", "PageID"), windowSize = i)
      .exec(filePath = "E:\\Allen\\NYTFuture\\NYT_result_2.0b\\NYT_slidingWindow_size" + i + ".txt")
  }
}
