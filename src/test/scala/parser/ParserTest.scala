package parser

import java.nio.file.{Files, Paths}

import files.{Doc, DataContainer}
import files.filetypes.CSV
import org.scalatest.FlatSpec
import parser.implementations.stanford.TregexMatcher
import parser.processing.Processing
import utils.OnStartUp._
import parser.ParserType._
import files.DataContainerTypes._

import scala.collection.immutable.HashMap

/**
 * Created by anie on 3/26/2015.
 */
class ParserTest extends FlatSpec {

  "A parser" should "function well" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with CSV with Doc
    val parser = new Parser(doc) with CSV with FileBuffer with TregexMatcher with Processing


    def printSuffix(par: Parser with CSV with FileBuffer): Unit = {
      println(par.typesuffix.head)
    }
  }

  "combine" should "combine tow NormalRow on left" in {
    val normalRow: NormalRow = Left(Vector("1", "2", "3", "4", "5", "6", "7"))
    val normalRow2: NormalRow = Left(Vector("1", "2", "3", "4", "5", "6", "7"))
    val combined = combine(normalRow, normalRow2)
    println(combined.left.get)
  }

  it should "combine two NormalRow on right" in {
    val normalRow: NormalRow = Right(Map("A" -> "1", "B" -> "2", "C" -> "3"))
    val normalRow2: NormalRow = Right(Map("D" -> "1", "E" -> "2", "F" -> "3"))
    val combined = combine(normalRow, normalRow2)
    println(combined.right.get)
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
    val tempFile = Paths.get(System.getProperty("user.home")).resolve(".Tinker")
//    Files.createDirectory(tempFile)
    Files.createTempFile(tempFile, "parser", ".csv")
  }

}