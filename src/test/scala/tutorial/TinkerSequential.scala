package tutorial

import core.DataContainer
import files.filetypes.input._
import files.filetypes.output.{TabOutput, CSVOutput}
import org.scalatest.FlatSpec
import processing.buffers.file.FileBuffer
import processing.{Scheduler, Sequential}
import utils.ParameterCallToOption.Implicits._

/**
 * Created by Aimingnie on 6/29/15
 */
object TinkerSequential extends App {
  
  def convertFromTabToCSV(): Unit = {
    val scheduler = new Scheduler with FileBuffer with Sequential with CSVOutput
    val data = new DataContainer("./src/test/scala/tutorial/data/tabFile.tab", header = true) with Tab

    data.save("./src/test/scala/tutorial/data/generatedCSV.csv")
  }

  def convertFromCSVToTab(): Unit = {
    val scheduler = new Scheduler with FileBuffer with Sequential with TabOutput
    val data = new DataContainer("./src/test/scala/tutorial/data/csvFile.csv", header = true) with CSV

    data.save("./src/test/scala/tutorial/data/generatedTab.tab")
  }

}
