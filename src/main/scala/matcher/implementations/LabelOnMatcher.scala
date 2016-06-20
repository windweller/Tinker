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
        val key = if (parts.size > 1) parts(1).trim else value.replaceAll(" ", "_").trim
        patterns.put(key, pattern)
      }}

      scheduler.addToGraph(row => scala.concurrent.Future {
        if(Application.verbose) Timer.completeOne
        val tree = Tree.valueOf(row("parsed"))
        if(tree == null) { fail("No tree found for \""+row("parsed")+"\"") }
        else { row += ("matched" -> search(struct.getTargetValue(row).getOrElse("sentence"), tree, patterns)) }
        row
      })
    }
    this
  }

  private def search(sentence: String, tree: Tree, patterns: mutable.HashMap[String,TregexPattern]): String = {
    if(sentence == null) fail("No sentences found")

    val matched = ListBuffer[Tuple2[Int, String]]()

    patterns.foreach { i =>
      try {
        val matcher = i._2.matcher(tree)
        if (matcher.find()) {
          var found = ""
          //Gives the words triggered by the rule
          if(matcher.getMatch.children().nonEmpty) {
            val leaves = matcher.getMatch.yieldWords().iterator()
            var rec = 0
            //Creates the part which was triggered by the rule
            while(leaves.hasNext && rec < 5) {
              found += leaves.next().word()
              found += " "
              rec+=1
            }
          }

          //Find the index in sentence of the first word triggered by the rule
          //val foundbeginpos = sentence.indexOf(found)
          //val foundi = sentence.indexOf(" "+found.trim)
          val foundInSent = s"\\b${found.trim}\\b".r.findAllIn(sentence)
          //val foundInSent = found.r.findAllIn(sentence)
          while(foundInSent.hasNext) {
            matched += foundInSent.start -> i._1
            foundInSent.next()
          }
          //if not found, give the index of the sentence's middle part
          if(matcher.find()) {
            val foundfirstblank = sentence.indexOf(" ", sentence.length/2)
            if(foundfirstblank >= 0 ) { matched += foundfirstblank -> i._1 }
            else { matched += 0 -> i._1 }
          }
        }
      } catch {
        case e: NullPointerException =>
          fail("tree error with \""+ sentence+"\"")
      }
    }

    //Only one rule was triggered
    if(matched.length == 1) {
      if(matched.head._1 == 0) return "$" + matched.head._2+" " + sentence
      val subs = sentence.substring(0,matched.head._1)
      val result = subs + " $"+matched.head._2+" " + sentence.substring(matched.head._1, sentence.length-1)
      return result
    }
    //Several rules triggered
    else if (matched.length > 1) {
      val sorted = matched.sortBy(_._1)
      var prec = 0
      var resultmultiple = ""
      sorted.foreach { i =>
        if(i._1 == 0) {
          resultmultiple = resultmultiple.concat("$"+i._2+" ")
        }
        else {
          val rule = " $"+i._2+" "
          resultmultiple = resultmultiple.concat(sentence.substring(prec,i._1)).concat(rule)
        }
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