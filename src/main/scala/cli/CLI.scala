package cli

import core.DataContainer
import core.structure.DataStructure
import files.filetypes.input.CSV
import org.apache.commons.cli._
import utils.ParameterCallToOption.Implicits._


/**
 * Created by anie on 8/31/2015.
 */
object CLI {

  def main(args: Array[String]) {
    val parser = new GnuParser()
    val options = new Options()

    options.addOption("f", "file", true, "specify a file used for classification")
    options.addOption("t", "timer", true, "specify a file location for timer")
    options.addOption("o", "output", true, "specify a file location for output")
    options.addOption("rf", "frule", true, "specify the future rule file")
    options.addOption("rp", "prule", true, "specify the past rule file")
    options.addOption("c", "core", true, "specify the number of cores to run on")
    options.addOption("h", "header", false, "specify if the file has header or not")
    options.addOption("fm", "format", true, "specify the format of file: csv or tab")

    var cmd: CommandLine = null
    try {cmd = parser.parse(options, args)}
    catch{
      case ex: Exception =>usage(options)
    }

    val data = new DataContainer("E:\\Allen\\R\\emnlp2015\\tweetsByCountryCharacterFilteredEmoticonRemoved.csv",
                                  header = cmd.hasOption("h"), core = cmd.getOptionValue("c").toInt) with CSV
    val struct = new DataStructure(targetColumnWithName = "Tweet", idColumnWithName = "State")

  }

  def usage(options: Options) {
    val formatter = new HelpFormatter()
    formatter.printHelp("Mallet Classifier", options)
  }

}
