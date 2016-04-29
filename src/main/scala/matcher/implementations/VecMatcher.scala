package matcher.implementations

import application.Application
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.DataContainer
import files.structure.DataSelect
import matcher.Matcher
import utils.FailureHandle

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Aurore on 4/17/16
  */
trait VecMatcher extends Matcher with FailureHandle {

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
      //On récupère ici le nom des règles et les règles

      val patterns = mutable.HashMap.empty[String, TregexPattern]
      val patternsText = rulesFromFile(file).getOrElse(patternsRaw.get)

      patternsText.foreach { line => scala.concurrent.Future {
          val parts = line.split("->")
          val value = parts(0).trim
          val key = if (parts.size > 1) parts(1).trim
                    else value
          val pattern = if (tnterms.nonEmpty || tcterms.nonEmpty) TregexPattern.compile(preprocessTregex(value))
                        else  TregexPattern.compile(value)
          patterns.put(key, pattern)
        }
      }

      //On ajoute au graphe une ligne
      scheduler.addToGraph(row => scala.concurrent.Future {
        val tree = Tree.valueOf(struct.getTargetValue(row).getOrElse(row("parsed")))
        //On ajoute à la ligne une colonne avec en titre la règle
        row += ("matched" -> search(struct.getTargetValue(row).get, tree, patterns))
        row
      })
    }
    this
  }

  private def search(sentence: String, tree: Tree, patterns: mutable.HashMap[String,TregexPattern]): String = {
    val rulesmatched = ""
    var modifiedsentence = ""
    patterns.values.foreach { i =>
      try {
        val matcher = i.matcher(tree)
        if (matcher.find()) {

          //on ne va pas incrémenter mais garder le nom de la règle
          rulesmatched.concat('$'+i.toString)
        }
      } catch {
        case e: NullPointerException =>
          return "tree error"
          //this happens when a tree is malformed
          //we will not add any number to stats, just return it as is
          //println("NULL Pointer with " + tree.toString)
      }
    }
    modifiedsentence
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
