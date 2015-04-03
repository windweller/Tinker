package processing

import files.{Doc, DataContainer}
import files.filetypes.Tab
import parser.implementations.stanford.TregexMatcher
import parser.Parser
import processing.buffers.FileBuffer
import utils.ParameterCallToOption.implicits._

/**
 * Created by anie on 4/2/2015
 *
 * This is the test for parallel processing
 *
 */
object ParallelProcessingTest extends App {

  val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\NYTimes.tab", true) with Tab with Doc
  val parser = new Parser(doc,
    outputFile = "E:\\Allen\\NYTFuture\\NYT_sample\\experiment.txt",
    outputOverride = true,
    rules = Vector(
      "(VP < (VBG < going) < (S < (VP < TO)))",
      "(VP < (VBG < going) > (PP < TO))",
      "MD < will",
      "MD < ‘ll’",
      "MD < shall",
      "MD < would",
      "MD < 'd’",
      "VP < VBD << would",
      "MD < may")) with Tab with FileBuffer with TregexMatcher with Parallel

  parser.matches(rowStr = "Parse", useGeneratedRow = false)
  parser.exec()

}
