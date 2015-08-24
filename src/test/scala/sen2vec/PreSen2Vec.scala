package sen2vec

import java.io.{File, BufferedWriter, FileWriter}

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable

/**
 * Created by anie on 8/20/2015
 *
 * This now creates a directory that contains files presenting rules
 *
 * file name would be rule name
 */
object PreSen2Vec extends App {

  val of = scala.io.Source.fromFile("E:\\Allen\\R\\emnlp2015\\preword2vecSynLexWithoutRuleLexicon.txt")

  val corpus = mutable.HashMap.empty[String, Vector[String]]

  var a = 0

  val pattern = "^\\$R".r

  of.getLines().foreach { line =>
    val encounteredRule = mutable.HashSet.empty[String]
    line.split(" ").foreach { word =>
      if (pattern.findFirstIn(word).nonEmpty && !encounteredRule.contains(word)) {
        encounteredRule += word
        if (corpus.get(word).isEmpty) corpus += word -> Vector(line.replaceAll("\\"+word + " ", ""))
        else corpus(word) = corpus(word) :+ line.replaceAll("\\"+word + " ", "")
      }
    }
    a += 1
    if (a % 10000 == 0) println(a)
  }

  val dir = "E:\\Allen\\R\\emnlp2015\\word2vec\\syn_lex\\"

  corpus.foreach { case (index, sentences) =>

    val file = new File(dir + index + ".txt")
    val bw = new BufferedWriter(new FileWriter(file))

    sentences.foreach { sentence =>
      bw.write(sentence + "\r\n")
    }
    bw.close()
  }
}
