package files.filetypes.output

import core.DataContainer
import files.filetypes.input.Tab
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.Implicits._


/**
 * Created by Aimingnie on 7/2/15.
 */
class CSVOutputTest extends FlatSpec with CSVOutput {

  behavior of "CSVOutputTest"

  it should "encode" in {
    val data = new DataContainer("./src/test/scala/tutorial/data/tabFile.tab", header = true) with Tab
    val row = data.data.next()
    val result = encodeHeader(row)
    println(result(0))
  }

  it should "encodeHeader" in {
    val data = new DataContainer("./src/test/scala/tutorial/data/tabFile.tab", header = true) with Tab
    val row = data.data.next()
    val result = encode(row)
    println(result)
  }

}
