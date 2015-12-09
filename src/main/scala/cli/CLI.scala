package cli

import java.util.PriorityQueue
import java_utils.StringUtils

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap

/**
  * Created by Aimingnie on 11/29/15.
  * This relies on Java code from Stanford NLP...should be changed
  */
object CLI {

  def main(args: Array[String]) {
    //--Get Properties
    val props = StringUtils.argsToProperties(args)
    //(order keys)
    val keys = new PriorityQueue[String]()
    var maxLength: Int = 0
    val propit = props.keySet().iterator()
    while (propit.hasNext) {
      val key = propit.next()
      keys.add(key.toString)
      maxLength = Math.max(maxLength, key.toString.length)
    }

    System.out.println("--------------------")
    System.out.println(" Tinker CLI")
    System.out.println("--------------------")
    System.out.println("Options:")

    val kit = keys.iterator()
    while (kit.hasNext) {
      val key = kit.next()
      System.out.print("  -" + key)
      (0 to maxLength - key.length).foreach(_ => print(" "))
      println("    " + props.getProperty(key))
    }

    println()

    // ==== Analyzing commands and build up tasks =====
    // currently offer 3 options:
    // 1. Parsing
    // 2. Matching
    // 3. Combined
    // 4. Help
    val help = props.getProperty("help")
    if (help == "true") {
      println("Available Options: ")
      val dict = HashMap(
        "help" -> "print help guide",
        "tokenizeColumn" -> "use as '-tokenizeColumn 1', you are allowed to use name",
        "parseColumn" -> "use as '-parseColumn 2', if used as '-parseColumn t', then it's parsing tokenized column",
        "matchColumn" -> "use as '-matchColumn 3', if used as '-matchColumn p', then it's matching the previously parsed column",
        "rule" -> "use as '--rule path/to/rule/file'",
        "tn" -> "use as '--tn file/path/tn.txt' point out vocabularies to replace TN",
        "tc" -> "use as '--tc file/path/tc.txt' point out vocabularies to replace TC",
        "core" -> "use as '-core 4', specify the number of cores you want to run on"
      )
      dict.foreach { case (key, value) =>
        System.out.print("  -" + key)
        (0 to maxLength - key.length).foreach(_ => print(" "))
        println("    " + value)
      }
      println()
      System.exit(0)
    }

    System.out.println("Creating Tinker class...")


  }

}