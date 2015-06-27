package files

import files.filetypes.csv.CSV
import files.operations.FileOp
import newProcessing.buffers.file.FileBuffer
import newProcessing.{Parallel, Operation}
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.implicits._

/**
 * Created by anie on 4/22/2015.
 */
class DataContainerTest extends FlatSpec {

  "assembled object" should "work" in {
    val data = new DataContainer("") with CSV with FileOp


  }

}