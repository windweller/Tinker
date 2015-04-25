package newFiles

import newFiles.filetypes.csv.CSV
import newFiles.operations.FileOp
import newProcessing.buffers.FileBuffer
import newProcessing.{Parallel, Operation}
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.implicits._

/**
 * Created by anie on 4/22/2015.
 */
class DataContainerTest extends FlatSpec {

  "assembled object" should "work" in {
    val data = new DataContainer("") with CSV with FileOp with Operation with Parallel with FileBuffer


  }

}
