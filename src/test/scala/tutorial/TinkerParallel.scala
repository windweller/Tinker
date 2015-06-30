package tutorial

import akka.actor.ActorSystem
import akka.testkit.TestKit
import files.DataContainer
import files.filetypes.input.CSV
import files.filetypes.output.TabOutput
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}
import processing.buffers.file.FileBuffer
import processing.{Parallel, Scheduler}
import utils.ParameterCallToOption.Implicits._

/**
 * Created by Aimingnie on 6/30/15
 *
 * Parallel processing cannot be
 */
class TinkerParallel extends TestKit(ActorSystem("testsystem"))
                          with FlatSpecLike with BeforeAndAfterAll {

  "parallel processing" can "transform csv file into tab format" in {
    val scheduler = new Scheduler(4) with FileBuffer with Parallel with TabOutput
    val data = new DataContainer("./src/test/scala/tutorial/data/csvFile.csv", header = true)(scheduler) with CSV

    data.save("./src/test/scala/tutorial/data/generatedTab.tab")
  }

}
