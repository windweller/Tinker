package application

import java.io.File

/**
  * Created by aurore on 19/01/16.
  */
object Application {
  var version = getClass.getPackage.getImplementationVersion

  def main(args: Array[String]) {
    val v = if (version == null) "0.1" else version
    val parser = new scopt.OptionParser[Config]("tinker") {
      head("Tinker", v)

      opt[File]('i', "in") required() valueName("<file>") action { (x, c) =>
        c.copy(out = x) } text("file with line-separated sentences, required")
      opt[File]('o', "out") required() valueName("<file>") action { (x, c) =>
        c.copy(out = x) } text("file for the output, required")
      opt[File]('r', "rules") valueName("<file>") action { (x, c) =>
        c.copy(out = x) } text("file with line-separated tregex rules, required if option --matched selected")
      opt[Unit]('c', "clean") action { (_, c) =>
        c.copy(clean = true) } text("clean is a flag")
      opt[Unit]('p', "parse") action { (_, c) =>
        c.copy(parse = true) } text("parse is a flag")
      opt[Unit]('m',"matched") action { (_, c) =>
        c.copy(matched = true) } text("matched is a flag")
      checkConfig { c =>
        if (!c.clean && !c.parse && !c.matched)
          failure("choice command: clean, parse or matched") else success
      }

      help("help") text("prints this usage text")
    }

    // parser.parse returns Option[C]
    val config = parser.parse(args, Config()).getOrElse {
      sys.exit(1)
    }


  }
}
