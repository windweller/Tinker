package tutorial

import files.DataContainer
import files.filetypes.format._
import utils.ParameterCallToOption.implicits._


import org.scalatest.FlatSpec

/**
 * Created by Aimingnie on 6/27/15.
 */
class TinkerCore extends FlatSpec {

  ///Users/Aimingnie/Desktop/hackathon/tinker

  "tinker-core" can "read in file in predefined format" in {
    val workerBook = new DataContainer("./src/test/scala/tutorial/tabFile.tab", header = true) with Tab
    val workerBook2 = new DataContainer("./src/test/scala/tutorial/csvFile.csv", header = true) with CSV
    workerBook.data.foreach(e => println(e))
    workerBook2.data.foreach(e => println(e))
  }

}
