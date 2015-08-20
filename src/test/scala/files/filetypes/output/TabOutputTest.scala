package files.filetypes.output

import core.DataContainer
import files.filetypes.input.Tab
import core.structure.DataStructure
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.Implicits._

/**
 * Created by Aimingnie on 7/1/15.
 */
class TabOutputTest extends FlatSpec with TabOutput  {

  behavior of "TabOutputTest"

  it should "encode header" in {
    val struct = new DataStructure(ignoreColumnsWithName = Vector("CodeNumber"))
    val data = new DataContainer("./src/test/scala/tutorial/data/tabFile.tab", header = true) with Tab
    val row = data.data.next()
    val result = encodeHeader(row, Some(struct))
    println(result(0))
  }

  it should "encode" in {
    val struct = new DataStructure(ignoreColumnsWithName = Vector("CodeNumber"))
    val data = new DataContainer("./src/test/scala/tutorial/data/tabFile.tab", header = true) with Tab
    val row = data.data.next()
    val result = encode(row, Some(struct))
    println(result)
  }

}
