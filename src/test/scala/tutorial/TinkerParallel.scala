package tutorial

import files.DataContainer
import files.filetypes.input.CSV
import files.filetypes.output.TabOutput
import processing.buffers.file.FileBuffer
import processing.{Parallel, Scheduler}
import utils.ParameterCallToOption.Implicits._

/**
 * Created by Aimingnie on 6/30/15
 *
 * Parallel processing cannot be
 */
object TinkerParallel extends App {

      val scheduler = new Scheduler(4) with FileBuffer with Parallel with TabOutput
      val data = new DataContainer("./src/test/scala/tutorial/data/csvFile.csv", header = true) with CSV

      data.save("./src/test/scala/tutorial/data/generatedTab.tab" )

}