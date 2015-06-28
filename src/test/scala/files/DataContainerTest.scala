package files

import files.filetypes.format.CSV
import files.operations.FileOp
import processing.buffers.file.FileBuffer
import processing.{Parallel, Operation}
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
