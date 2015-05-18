package newProcessing

import newFiles.DataContainer
import newFiles.filetypes.csv.CSV
import newFiles.operations.FileOp
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

}
