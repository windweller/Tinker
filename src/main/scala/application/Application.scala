package application

import java.io.File
import java.net.URL

import files.DataContainer
import files.filetypes.input.{CSV, Tab}
import files.operations.FileOp
import files.structure.{DataSelect, DataStructure}
import matcher.implementations.{FutureTregexMatcher, LabelOnMatcher}
import nlp.preprocess.filters.{Filter, TwitterNewFilter}
import parser.implementations.StanfordNLP.EnglishPCFGParser
import utils.ParameterCallToOption.Implicits._

/**
  * Created by aurore on 19/01/16.
  */
object Application extends App {
  val v = if (version == null) "0.1" else version
  val parser = new scopt.OptionParser[Config]("tinker") {
    head("Tinker", v)

    opt[File]('i', "in") required() valueName("<file>") action { (x, c) =>
      c.copy(in = x) } text("file with line-separated sentences, required")
    opt[File]('o', "out") required() valueName("<file>") action { (x, c) =>
      c.copy(out = x) } text("file for the output, required")
    opt[String]('n',"name") required() action { (x, c) =>
      c.copy(namecolumn = x) } text("column name where to find text for `clean` and `parse`, tree for `match`, required")
    opt[Int]("cores") action {(x, c) =>
      c.copy(numcore = x) } text("number of cores")
    note("")
    cmd("clean") action { (_, c) =>
      c.copy(mode = "c") } text("clean is a command to cleaning corpus tweets.\nIt only retains input file name and cleaned text by default.") children(
      opt[Seq[String]]("keep") valueName("<value>,<value>...") action { (k, c) =>
        c.copy(keep = k) } text("keep columns names, separated by commas")
      )
    note("")
    cmd("parse") action { (_, c) =>
      c.copy(mode = "p") } text("parse is a command to parsing with Stanford Parser.")
    note("")
    cmd("match") action { (_, c) =>
      c.copy(mode = "m") } text("match is a command to find Tregex rules on texts.") children(
      opt[File]('r', "rules") required() valueName("<file>") action { (x, c) =>
        c.copy(rules = x) } text("file with line-separated tregex rules, required"))
      opt[Unit]("label") action { (x, c) =>
        c.copy(label = true) } text("output contains for each line a sentence-labeled column")

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
  var version = getClass.getPackage.getImplementationVersion

  if(config.in.toString.endsWith("tab")) {
    if(config.mode.equals("c")) clean_tab(config.in, config.out, config.namecolumn, config.numcore, config.keep)
    if(config.mode.equals("p")) parse_tab(config.in, config.out, config.namecolumn, config.numcore)
    if(config.mode.equals("m")) {
      if(config.label)
        matchedlabel_tab(config.in, config.out, config.rules, config.namecolumn, config.numcore)
      else matched_tab(config.in, config.out, config.rules, config.namecolumn, config.numcore)
    }
  }
  else {
    if(config.mode.equals("c")) clean_csv(config.in, config.out, config.namecolumn, config.numcore, config.keep)
    if(config.mode.equals("p")) parse_csv(config.in, config.out, config.namecolumn, config.numcore)
    if(config.mode.equals("m")) {
      if(config.label)
        matchedlabel_csv(config.in, config.out, config.rules, config.namecolumn, config.numcore)
      else matched_csv(config.in, config.out, config.rules, config.namecolumn, config.numcore)
    }
  }

  def clean_csv(input: File, output: File, namecolumn: String, numcore: Int, keepColumns: Seq[String]): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with CSV

    val struct = new DataStructure(targetColumnWithName = namecolumn,
      keepColumnsWithNames = keepColumns.toIndexedSeq)

    val filter = new Filter(data, struct) with TwitterNewFilter
    filter.preprocess(output.toString)
  }

  def clean_tab(input: File, output: File, namecolumn: String, numcore: Int, keepColumns: Seq[String]): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab

    val struct = new DataStructure(targetColumnWithName = namecolumn,
      keepColumnsWithNames = keepColumns.toIndexedSeq)

    val filter = new Filter(data, struct) with TwitterNewFilter
    filter.preprocess(output.toString)
  }

  def parse_tab(input: File, output: File, namecolumn: String, numcore: Int): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab with EnglishPCFGParser with FileOp

    data.parse(None, DataSelect(targetColumnWithName = namecolumn))
    data.save(output.toString)
  }

  def parse_csv(input: File, output: File, namecolumn: String, numcore: Int): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with CSV with EnglishPCFGParser with FileOp

    data.parse(None, DataSelect(targetColumnWithName = namecolumn))
    data.save(output.toString)
  }

  def matched_csv(input: File, output: File, rules: File, namecolumn: String, numcore: Int): Unit = {

    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with CSV with FutureTregexMatcher with EnglishPCFGParser with FileOp

    data.matcher(rules.toString, None, DataSelect(targetColumnWithName = namecolumn))
    data.save(output.toString)
  }

  def matched_tab(input: File, output: File, rules: File, namecolumn: String, numcore: Int): Unit = {

    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab with FutureTregexMatcher with EnglishPCFGParser with FileOp

    data.matcher(rules.toString, None, DataSelect(targetColumnWithName = namecolumn))
    data.save(output.toString)
  }

  def matchedlabel_csv(input: File, output: File, rules: File, namecolumn: String, numcore: Int): Unit = {

    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with CSV with LabelOnMatcher with EnglishPCFGParser with FileOp

    data.matcher(rules.toString, None, DataSelect(targetColumnWithName = namecolumn))
    data.save(output.toString)
  }

  def matchedlabel_tab(input: File, output: File, rules: File, namecolumn: String, numcore: Int): Unit = {

    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab with LabelOnMatcher with EnglishPCFGParser with FileOp

    data.matcher(rules.toString, None, DataSelect(targetColumnWithName = namecolumn))
    data.save(output.toString)
  }

  def file(path: String): URL =
    getClass().getClassLoader().getResource(path)
}
