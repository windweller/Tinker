package files

import files.filetypes.input.CSV
import files.operations.FileOp
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 4/22/2015.
 */
class DataContainerTest extends FlatSpec {

  "assembled object" should "work" in {
    val data = new DataContainer("") with CSV with FileOp


  }

}
