package application

import java.io.File
import java.net.URL

import cleaner.implementations.CharacterCleaner
import files.DataContainer
import files.filetypes.input.{CSV, Tab}
import files.operations.FileOp
import files.structure.DataSelect
import matcher.implementations.{FutureTregexMatcher, LabelOnMatcher}
import parser.implementations.StanfordNLP.EnglishPCFGParser
import utils.ParameterCallToOption.Implicits._

/**
  * Created by aurore on 19/01/16.
  */
object Application extends App {
  val version = getClass.getPackage.getImplementationVersion
  val v = if (version == null) "0.1" else version
  val parser = new scopt.OptionParser[Config]("tinker") {
    head("Tinker", v)

    help("help") text("prints this usage text")

    opt[File]('i', "in") required() valueName("<file>") action { (x, c) =>
      c.copy(in = x) } text("file with line-separated sentences, required")

    opt[File]('o', "out") required() valueName("<file>") action { (x, c) =>
      c.copy(out = x) } text("file for the output, required")

    opt[String]('t',"text") required() action { (x, c) =>
      c.copy(text = x) } text("column name where to find text, required")

    opt[String]("tree") action { (k,c) =>
      c.copy(tree = k) } text("column name where to find parsed tree, `parsed` by default")

    opt[Int]("cores") action {(x, c) =>
      c.copy(numcore = x) } text("number of cores")

    opt[Unit]('v', "verbose") action { (_, c) =>
      c.copy(verbose = true) } text("verbose option\n")

    cmd("clean") action { (_, c) =>
      c.copy(mode = "c") } text("clean is a command to cleaning corpus tweets.\nIt only retains input file name and cleaned text by default.") children(
      opt[Seq[String]]("keep") valueName("<value>,<value>...") action { (k, c) =>
        c.copy(keep = k) } text("keep columns names, separated by commas\n")
      )

    cmd("parse") action { (_, c) =>
      c.copy(mode = "p") } text("parse is a command to parsing with Stanford Parser\n")

    cmd("match") action { (_, c) =>
      c.copy(mode = "m") } text("match is a command to find Tregex rules on texts, each column by rule") children(
      opt[File]('r', "rules") required() valueName("<file>") action { (x, c) =>
        c.copy(rules = x) } text("file with line-separated tregex rules, required\n"))

    cmd("label") action { (_, c) =>
      c.copy(mode = "l") } text("label is a command to find Tregex rules on texts, inserts labels on the sentence") children(
      opt[File]('r', "rules") required() valueName("<file>") action { (x, c) =>
        c.copy(rules = x) } text("file with line-separated tregex rules, required\n"))

    checkConfig { c =>
      if (c.mode.equals("")) failure("please choose a command, like `clean`")
      else if(!c.in.exists()) failure("input not found")
      else if(c.out.toString.isEmpty || c.out.isDirectory) failure("output is not a filename")
      else success
    }
  }
  // parser.parse returns Option[C]
  val config = parser.parse(args, Config()).getOrElse {
    sys.exit(1)
  }

  if(config.in.toString.endsWith("tab")) {
    if(config.mode.equals("c")) clean_tab(config.in, config.out, config.text, config.numcore, config.keep)
    if(config.mode.equals("p")) parse_tab(config.in, config.out, config.text, config.numcore)
    if(config.mode.equals("m")) matched_tab(config.in, config.out, config.rules, config.tree, config.numcore)
    if(config.mode.equals("l")) matchedlabel_tab(config.in, config.out, config.rules, config.tree, config.text, config.numcore)
  }
  else {
    if(config.mode.equals("c")) clean_csv(config.in, config.out, config.text, config.numcore, config.keep)
    if(config.mode.equals("p")) parse_csv(config.in, config.out, config.text, config.numcore)
    if(config.mode.equals("m")) matched_csv(config.in, config.out, config.rules, config.tree, config.numcore)
    if(config.mode.equals("l")) matchedlabel_csv(config.in, config.out, config.rules, config.tree, config.text, config.numcore)
  }

  def clean_csv(input: File, output: File, text: String, numcore: Int, keepColumns: Seq[String]): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with CSV with CharacterCleaner

    data.clean(None, DataSelect(targetColumnWithName = text))
    save(output, data)
  }

  def clean_tab(input: File, output: File, text: String, numcore: Int, keepColumns: Seq[String]): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab with CharacterCleaner

    data.clean(None, DataSelect(targetColumnWithName = text))
    save(output, data)
  }

  def parse_tab(input: File, output: File, text: String, numcore: Int): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab with EnglishPCFGParser with FileOp

    data.parse(None, DataSelect(targetColumnWithName = text))
    save(output, data)
  }

  def parse_csv(input: File, output: File, text: String, numcore: Int): Unit = {
    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with CSV with EnglishPCFGParser with FileOp

    data.parse(None, DataSelect(targetColumnWithName = text))
    save(output, data)
  }

  def matched_csv(input: File, output: File, rules: File, tree: String, numcore: Int): Unit = {

    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with CSV with FutureTregexMatcher with EnglishPCFGParser with FileOp

    data.matcher(rules.toString, None, DataSelect(targetColumnWithName = tree))
    save(output, data)
  }

  def matched_tab(input: File, output: File, rules: File, tree: String, numcore: Int): Unit = {

    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab with FutureTregexMatcher with EnglishPCFGParser with FileOp

    data.matcher(rules.toString, None, DataSelect(targetColumnWithName = tree))
    save(output, data)
  }

  def matchedlabel_csv(input: File, output: File, rules: File, tree: String, text: String, numcore: Int): Unit = {

    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with CSV with LabelOnMatcher with EnglishPCFGParser with FileOp

    data.matcher(rules.toString, None, DataSelect(targetColumnWithName = text))
    save(output, data)
  }

  def matchedlabel_tab(input: File, output: File, rules: File, tree: String, text: String, numcore: Int): Unit = {

    val data = new DataContainer(input.toString,
      header = true, core = numcore)
      with Tab with LabelOnMatcher with EnglishPCFGParser with FileOp

    data.matcher(rules.toString, None, DataSelect(targetColumnWithName = text))
    save(output, data)
  }

  def save(output: File, data: DataContainer): Unit = {
    if(output.toString.endsWith("tab")) {
      data.toTab
    }
    data.save(output.toString)
  }

  def file(path: String): URL =
    getClass().getClassLoader().getResource(path)

  def verbose: Boolean =
    config.verbose
}
