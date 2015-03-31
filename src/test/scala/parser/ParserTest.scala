package parser

import java.nio.file.Paths

import files.{Doc, DataContainer}
import files.filetypes.CSV
import org.scalatest.FlatSpec
import parser.implementations.stanford.TregexMatcher
import parser.processing.Processing
import utils.OnStartUp._

import scala.collection.immutable.HashMap

/**
 * Created by anie on 3/26/2015.
 */
class ParserTest extends FlatSpec {

  "A parser" should "function well" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with CSV with Doc
    val parser = new Parser(doc) with TregexMatcher with CSV with Processing

  }

  "header printing with rows" should "be aligned" in {
    val maps = Array(Map("A" -> 1, "B" -> 2, "C" -> 3, "D" -> 4, "E" -> 5),
      Map("A" -> 1, "B" -> 2, "C" -> 3, "D" -> 4, "E" -> 5),
      Map("A" -> 1, "B" -> 2, "C" -> 3, "D" -> 4, "E" -> 5))

    println(maps.head.keys.mkString("\t"))
    maps.foreach(map => {
      println(map.values.mkString("\t"))
    })
    //we'll assume they work in order, but there really isn't any guarantee
  }

  "Path" should "create the right temp file" in {
    println(Paths.get(System.getProperty("user.home")).resolve("Tinker").resolve("parserTmp.tab").toString)
  }

}
