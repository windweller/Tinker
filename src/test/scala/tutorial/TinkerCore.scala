package tutorial

import files.DataContainer
import files.filetypes.input.{CSV, Tab}
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.Implicits._

/**
 * Created by Aimingnie on 6/27/15.
 */
class TinkerCore extends FlatSpec {

  "tinker-core" can "read in file in predefined format" in {
    val workerBook = new DataContainer("./src/test/scala/tutorial/data/tabFile.tab", header = true) with Tab
    val workerBook2 = new DataContainer("./src/test/scala/tutorial/data/csvFile.csv", header = true) with CSV
    workerBook.data.foreach(e => println(e))
    workerBook2.data.foreach(e => println(e))
  }

}
