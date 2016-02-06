package application

import java.io.File
import java.net.URL

import files.DataContainer
import files.filetypes.input.Tab
import files.operations.FileOp
import files.structure.{DataSelect, DataStructure}
import matcher.implementations.FutureTregexMatcher
import nlp.preprocess.filters.{Filter, TwitterNewFilter}
import parser.implementations.StanfordNLP.EnglishPCFGParser
import utils.ParameterCallToOption.Implicits._

/**
  * Created by aurore on 19/01/16.
  */
object Application extends App {
  var version = getClass.getPackage.getImplementationVersion

  val v = if (version == null) "0.1" else version
  val parser = new scopt.OptionParser[Config]("tinker") {
    head("Tinker", v)

    opt[File]('i', "in") required() valueName("<file>") action { (x, c) =>
      c.copy(in = x) } text("file with line-separated sentences, required")
    opt[File]('o', "out") required() valueName("<file>") action { (x, c) =>
      c.copy(out = x) } text("file for the output, required")
    opt[String]('n',"name") required() action { (x, c) =>
      c.copy(namecolumn = x) } text("column name where to find text, required")
    opt[Int]("cores") action {(x, c) =>
      c.copy(numcore = x) } text("number of cores")

    cmd("clean") action { (_, c) =>
      c.copy(mode = "c") } text("clean is a command to cleaning corpus tweets.")

    cmd("parse") action { (_, c) =>
      c.copy(mode = "p") } text("parse is a command to parsing with Stanford Parser.")

    cmd("match") action { (_, c) =>
      c.copy(mode = "m") } text("match is a command to find Tregex rules on texts.") children(
      opt[File]('r', "rules") required() valueName("<file>") action { (x, c) =>
        c.copy(rules = x) } text("file with line-separated tregex rules, required")
      )

    checkConfig { c =>
      if (c.mode.equals("")) failure("please choose a command, like clean")
      else if(!c.in.exists()) {
        failure("input not found")
      }
      else if(c.out.toString.isEmpty || c.out.isDirectory) {
        failure("output is not a filename")
      }
      else success
    }

    help("help") text("prints this usage text")
  }

  // parser.parse returns Option[C]
  val config = parser.parse(args, Config()).getOrElse {
    sys.exit(1)
  }

  if(config.mode.equals("c")) clean(config.in, config.out, config.namecolumn, config.numcore)
  if(config.mode.equals("p")) parse(config.in, config.out, config.namecolumn, config.numcore)
  if(config.mode.equals("m")) matched(config.in, config.out, config.rules, config.namecolumn, config.numcore)

  def clean(input: File, output: File, namecolumn: String, numcore: Int): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab

    if(input.toString.endsWith("csv")) {
      data.toCSV
    }
    val struct = new DataStructure(targetColumnWithName = namecolumn)

    val filter = new Filter(data, struct) with TwitterNewFilter
    filter.preprocess(output.toString)
  }

  def parse(input: File, output: File, namecolumn: String, numcore: Int): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab with EnglishPCFGParser with FutureTregexMatcher with FileOp

    if(input.toString.endsWith("csv")) {
      data.toCSV
    }

    data.parse(None, DataSelect(targetColumnWithName = namecolumn))
    data.save(output.toString)
  }

  def matched(input: File, output: File, rules: File, namecolumn: String, numcore: Int): Unit = {

    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab with EnglishPCFGParser with FutureTregexMatcher with FileOp

    if(input.toString.endsWith("csv")) {
      data.toCSV
    }

    data.parse(None, DataSelect(targetColumnWithName = namecolumn))
    data.matcher(rules.toString)
    data.save(output.toString)
  }

  def file(path: String): URL =
    getClass().getClassLoader().getResource(path)
}
