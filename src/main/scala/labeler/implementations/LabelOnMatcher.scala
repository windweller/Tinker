package labeler.implementations

import application.Application
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.DataContainer
import files.structure.DataSelect
import labeler.Labeler
import utils.{FailureHandle, Timer}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex

/**
  * LabelOnMatcher Trait
  * Written by Aurore
  */
trait LabelOnMatcher extends Labeler with FailureHandle {

  this: DataContainer =>
  val tnterms = scala.io.Source.fromURL(Application.file("TNTermsReduced2.txt"))
    .getLines().mkString("|")
  val tcterms = scala.io.Source.fromURL(Application.file("TNTermsReduced2.txt"))
      .getLines().mkString("|")

  override def label(file:Option[String] = None,
                       patternsRaw: Option[List[String]] = None,
                       text: DataSelect = DataSelect(),
                       tree: DataSelect = DataSelect(),
                       place: Option[String] = Some("start")): DataContainer with Labeler = {

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
        else { row += ("matched" -> findIn(text.getTargetValue(row).getOrElse("sentence"),
          Tree.valueOf(tree.getTargetValue(row).getOrElse("parsed")), patterns, place.get)) }
        row
      })
    }
    this
  }

  private def findIn(sentence: String, tree: Tree, patterns: mutable.HashMap[String,TregexPattern], place: String): String = {
    if (sentence == null) fail("no sentence found")

    val matched = ListBuffer[Tuple2[Int, String]]()
    val leaves = tree.yieldWords().toArray().mkString(" ")

    patterns.foreach { i =>
      try {
        val matcher = i._2.matcher(tree)
        while (matcher.find()) {
          //Gives the words triggered by the rule
          if(matcher.getMatch.children().nonEmpty) {
            val foundregex = "\\b"+Regex.quote(matcher.getMatch.yieldWords().toArray().mkString(" "))+"\\b"

            //Find the index in tree of the first word triggered by the rule
            val foundInLeaves = foundregex.r.findAllIn(leaves)
            //If not find, place at the start or end
            if(!foundInLeaves.hasNext) place match {
              case "end" => matched += sentence.length -> i._1
              case _ => matched += 0 -> i._1
            }

            //For each words found
            while(foundInLeaves.hasNext) {
              val blankpos = Some(findBlank(sentence,foundInLeaves.start,foundInLeaves.end,place)).map(x => {
                if(x < 0) place match { case "end" => sentence.length case _ => 0}
                else x
              })

              if(matched.indexOf(Tuple2(blankpos.get,i._1)) == -1) {
                matched += blankpos.get -> i._1
              }
              foundInLeaves.next
            }
          }
        }
      } catch {
        case e: NullPointerException =>
          fail("tree error with \""+ sentence+"\"")
      }
    }

    //Only one rule was triggered
    if(matched.length == 1) {
      if(matched.head._1 == 0) return "$" + matched.head._2 + " " + sentence
      val subs = sentence.substring(0,matched.head._1)
      val result = subs + " $"+matched.head._2 + sentence.substring(matched.head._1, sentence.length)
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
          val rule = " $"+i._2
          resultmultiple = resultmultiple.concat(sentence.substring(prec,i._1)).concat(rule)
        }
        prec = i._1
      }
      if(prec < sentence.length-1) resultmultiple = resultmultiple.concat(sentence.substring(prec, sentence.length))
      return resultmultiple
    }
    sentence
  }

  private def findBlank(sentence: String, start: Int, end: Int, place: String): Int = {
    place match {
      case "start" => sentence.lastIndexOf(" ", start)
      case "middle" => sentence.indexOf(" ", end - start)
      case "end" => sentence.indexOf(" ", end)
      case _ => sentence.lastIndexOf(" ", start)
    }
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