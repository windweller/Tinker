package tutorial

import core.DataContainer
import files.filetypes.input.CSV
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 7/15/2015
 */
object TinkerFileOps extends App {

   //this did not pass test btw
   val data = new DataContainer("./src/test/scala/tutorial/data/csvFile.csv", header = true) with CSV
   data.toTab
   data.save("./src/test/scala/tutorial/data/converted.tab")

}
