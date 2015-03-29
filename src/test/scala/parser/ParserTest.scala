package parser

import files.{Doc, DataContainer}
import files.filetypes.CSV
import org.scalatest.FlatSpec
import parser.implementations.stanford.TregexMatcher
import parser.processing.Processing
import utils.OnStartUp._

/**
 * Created by anie on 3/26/2015.
 */
class ParserTest extends FlatSpec {

  "A parser" should "function well" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with CSV with Doc
    val parser = new Parser(doc) with TregexMatcher with Processing

  }

}
