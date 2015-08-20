package files

import core.DataContainer
import files.filetypes.input.CSV
import files.operations.FileOp
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 4/22/2015.
 */
class DataContainerTest extends FlatSpec {

  "data container" should "work" in {
    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\Country_Tweets\\Tweets") with CSV
    print(data.strip.size)

  }

}
