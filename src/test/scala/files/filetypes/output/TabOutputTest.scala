package files.filetypes.output

import core.DataContainer
import files.filetypes.input.Tab
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.Implicits._

/**
 * Created by Aimingnie on 7/1/15.
 */
class TabOutputTest extends FlatSpec with TabOutput  {

  behavior of "TabOutputTest"

  it should "encode header" in {
    val data = new DataContainer("./src/test/scala/tutorial/data/tabFile.tab", header = true) with Tab
    val row = data.data.next()
    val result = encodeHeader(row)
    println(result(0))
  }

  it should "encode" in {
    val data = new DataContainer("./src/test/scala/tutorial/data/tabFile.tab", header = true) with Tab
    val row = data.data.next()
    val result = encode(row)
    println(result)
  }

}
