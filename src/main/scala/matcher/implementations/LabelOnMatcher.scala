package matcher.implementations

import application.Application
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.DataContainer
import files.structure.DataSelect
import matcher.Matcher
import utils.{FailureHandle, Timer}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Aurore on 4/17/16
  * This implementation creates a new column where all matched rules
  * will be inserted in the sentence as a flag $rule where it was first
  * triggered.
  */
trait LabelOnMatcher extends Matcher with FailureHandle {

  this: DataContainer =>
  val tnterms = scala.io.Source.fromURL(Application.file("TNTermsReduced2.txt"))
    .getLines().mkString("|") //getOrElse !!
  val tcterms = scala.io.Source.fromURL(Application.file("TNTermsReduced2.txt"))
      .getLines().mkString("|")

  override def matcher(file: Option[String] = None,
                       patternsRaw: Option[List[String]] = None,
                       struct: DataSelect = DataSelect()): DataContainer with Matcher = {

    if (patternsRaw.isEmpty && file.isEmpty) {
      fail("you must put in file or list of patterns")
    } else {
      val patterns = mutable.HashMap.empty[String, TregexPattern]
      val patternsText = rulesFromFile(file).getOrElse(patternsRaw.get)

      patternsText.foreach { line => {
        val parts = line.split("->")
        val value = parts(0).trim
        val pattern = if (tnterms.nonEmpty || tcterms.nonEmpty) TregexPattern.compile(preprocessTregex(value))
        else TregexPattern.compile(value)
        val key = if (parts.size > 1) parts(1).trim else value
        patterns.put(key, pattern)
      }}

      scheduler.addToGraph(row => scala.concurrent.Future {
        if(Application.verbose) Timer.completeOne
        val tree = Tree.valueOf(row("parsed"))
        row += ("matched" -> search(struct.getTargetValue(row).getOrElse("sentence"), tree, patterns))
        row
      })
    }
    this
  }

  private def search(sentence: String, tree: Tree, patterns: mutable.HashMap[String,TregexPattern]): String = {
    val matched = ListBuffer[Tuple2[Int, String]]()

    patterns.foreach { i =>
      try {
        val matcher = i._2.matcher(tree)
        if (matcher.find()) {
          val found = matcher.getMatch.children().map(x => x.value()).mkString(" ").trim
          val foundbeginpos = sentence.indexOf(found)
          //If it doesn't find the right text, it will put at the middle of the sentence
          if(foundbeginpos >= 0) { matched += foundbeginpos -> i._1 }
          else {
            val foundfirstblank = sentence.indexOf(" ", sentence.length/2)
            matched += foundfirstblank -> i._1
          }
        }
      } catch {
        case e: NullPointerException =>
          return "tree error"
      }
    }
    if(matched.length == 1) {
      /*val middle = sentence.length / 2;
      var blank = sentence.indexOf(' ',middle)
      if (blank.equals(0)) blank = sentence.indexOf(' ',0)
      val rule = " ["+matched.head._2+"] "
      return sentence.substring(0, blank) + rule + sentence.substring(blank, sentence.length-1)*/
      val subs = sentence.substring(0,matched.head._1)
      val rule = " $"+matched.head._2+" "
      val result = subs + rule + sentence.substring(matched.head._1, sentence.length-1)
      return result
    }
    else if (matched.length > 1) {
      val sorted = matched.sortBy(_._1)
      var prec = 0
      var resultmultiple = ""
      sorted.foreach { i =>
        val rule = " $"+i._2+" "
        resultmultiple = resultmultiple.concat(sentence.substring(prec,i._1)).concat(rule)
        prec = i._1
      }
      if(prec < sentence.length-1) resultmultiple = resultmultiple.concat(sentence.substring(prec, sentence.length-1))
      return resultmultiple
    }
    sentence
  }

  private def rulesFromFile(fileLoc: Option[String]): Option[List[String]] = {
    fileLoc.map(file => scala.io.Source.fromFile(file).getLines().toList)
  }

  private def preprocessTregex(pattern: String): String = {

    if (pattern.contains("TN|TC")) {
      return pattern.replaceAll("TN|TC", tnterms+"|"+tcterms)
    }
    else if (pattern.contains("TN")) {
      return pattern.replaceAll("TN", tnterms)
    }
    else if (pattern.contains("TC")) {
      return pattern.replaceAll("TC", tcterms)
    }
    else return pattern
  }
}
