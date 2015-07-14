package tutorial

import akka.actor.ActorSystem
import akka.testkit.TestKit
import files.DataContainer
import files.filetypes.input.CSV
import files.filetypes.output.TabOutput
import org.scalatest.{Matchers, MustMatchers, BeforeAndAfterAll, FlatSpecLike}
import processing.buffers.file.FileBuffer
import processing.{Parallel, Scheduler}
import utils.ActorSys
import utils.ParameterCallToOption.Implicits._

/**
 * Created by Aimingnie on 6/30/15
 *
 * Parallel processing cannot be
 */
object TinkerParallel extends App {


      val scheduler = new Scheduler(4) with FileBuffer with Parallel with TabOutput
      val data = new DataContainer("./src/test/scala/tutorial/data/csvFile.csv", header = true)(scheduler) with CSV

      data.save("./src/test/scala/tutorial/data/generatedTab.tab" )

}