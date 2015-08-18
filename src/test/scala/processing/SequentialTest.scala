package processing

import core.DataContainer
import files.filetypes.input.CSV
import files.filetypes.output.CSVOutput
import files.operations.FileOp
import org.scalatest.FlatSpec
import processing.buffers.file.FileBuffer
import utils.ParameterCallToOption.Implicits._

/**
 * Created by Aimingnie on 5/11/15
 */
class SequentialTest extends FlatSpec {

  "Sequential processing" should "work" in {
    //we are using SlidingWindow from FileOP
    val dc = new DataContainer(f = "E:\\Allen\\NYTFuture\\NYT_result_2.0b", header = true) with CSV with FileOp
    dc.compressBySlidingWindow(discardColsWithName = Vector("id", "ParagraphID", "PageID"), windowSize = 3)
      .exec(filePath = "E:\\Allen\\NYTFuture\\NYT_result_2.0b\\NYT_slidingWindow_size3.txt")
  }

  "Sequential processing in compressing future" should "work" in {
    val scheduler = new Scheduler(4) with FileBuffer with Sequential with CSVOutput
    val dc = new DataContainer(f = "E:\\Allen\\R\\acl2015\\twitterFuture2.csv", header = false) with CSV with FileOp
    dc.compressByAvg(groupByCol = 0, discardCols = Vector(1))
      .exec(filePath = "E:\\Allen\\R\\acl2015\\twitterFuture2Compressed.csv")
  }

}