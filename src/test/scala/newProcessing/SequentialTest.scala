package newProcessing

import newFiles.DataContainer
import newFiles.RowTypes.NormalRow
import newFiles.filetypes.csv.{CSVOutput, CSV}
import newFiles.filetypes.tab.TabOutput
import newFiles.operations.FileOp
import newProcessing.buffers.BufferConfig
import newProcessing.buffers.file.FileBuffer
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.implicits._

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
    val dc = new DataContainer(f = "E:\\Allen\\R\\acl2015\\twitterFuture2.csv", header = false)(scheduler) with CSV with FileOp
    dc.compressByAvg(groupByCol = 0, discardCols = Vector(1))
      .exec(filePath = "E:\\Allen\\R\\acl2015\\twitterFuture2Compressed.csv")
  }

}
