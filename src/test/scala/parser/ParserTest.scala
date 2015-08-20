package parser

import akka.actor.ActorSystem
import akka.testkit.TestKit
import core.DataContainer
import files.filetypes.input.{CSV, Tab}
import files.filetypes.output.CSVOutput
import core.structure.DataStructure
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, FlatSpec}
import processing.buffers.file.FileBuffer
import processing.{Parallel, Scheduler}
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 7/13/2015.
 */
class ParserTest extends TestKit(ActorSystem("testsystem"))
                      with FlatSpecLike with BeforeAndAfterAll {

  behavior of "Parser"

  it should "parse" in {
//    val scheduler = new Scheduler(15) with Parallel with FileBuffer with CSVOutput
//    val data = new DataContainer("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\nyt_by_sen2_2.csv", header = true)(scheduler) with CSV
//    val struct =  new DataStructure(idColumnWithName = "SentenceID", targetColumnWithName = "Sentence", keepColumnsWithNames = Vector("ParagraphID", "PageID"))
//    val parser = new Parser(data, struct)
//    data.exec("E:\\Allen\\NYTFuture\\NYT_sample_2.2tctn_reduced\\parse_file.csv")
  }

}
