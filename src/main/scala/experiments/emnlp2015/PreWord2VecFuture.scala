package experiments.emnlp2015

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import com.github.tototoshi.csv.CSVWriter
import com.typesafe.config.{ConfigFactory, Config}
import core.{RowTypes, DataContainer}
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import RowTypes._
import core.structure.DataStructure
import nlp.matcher.Matcher
import nlp.matcher.impl.Tregex
import utils.Timer

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
 * Created by anie on 8/4/2015.
 */
class PreWord2VecFuture(val data: DataContainer, val struct: DataStructure, val patternRaw: Array[String],
                        val tndoc: Option[String] = None, val tcdoc: Option[String] = None) {

  val matcher = new Matcher with Tregex
  //pre-processing patterns (replace TN|TC)
  val tnterms = tndoc.map(doc => scala.io.Source.fromFile(doc).getLines().toArray)
  val tcterms = tcdoc.map(doc => scala.io.Source.fromFile(doc).getLines().toArray)

  val patterns = if (tndoc.nonEmpty || tcdoc.nonEmpty) preprocessTregex(patternRaw).map(e => TregexPattern.compile(e))
  else  patternRaw.map(e => TregexPattern.compile(e))

  def preprocessTregex(patterns: Array[String]): Array[String] = {
    val result = ArrayBuffer.empty[String]
    patterns.foreach { p =>
      if (p.contains("TN|TC")) {
        //just replace all TN and TC
        //we are not replacing TN right now
        result ++= tnterms.get.map(tn => p.replace("TN|TC", tn))
        result ++= tcterms.get.map(tc => p.replace("TN|TC", tc))
      }
      else if (p.contains("TC|TN")) {
        result ++= tnterms.get.map(tn => p.replace("TC|TN", tn))
        result ++= tcterms.get.map(tc => p.replace("TC|TN", tc))
      }
      else if (p.contains("TN")) {
        result ++= tnterms.get.map(tn => p.replace("TN", tn))
      }
      else if (p.contains("TC")) {
        result ++= tcterms.get.map(tc => p.replace("TC", tc))
      }
      else result += p
    }
    result.toArray
  }

  private[this] def traverseTree(tree: Tree, patterns: Array[TregexPattern], blocks: ArrayBuffer[String]): Unit = {

    if (tree != null) {
      val children = tree.children()
      val it = children.iterator

      while (it.hasNext) {
        val currentTree = it.next()
        var added = false
        //start pattern matching
        (0 to patterns.length - 1).foreach { i =>
          val matcher = patterns(i).matcher(currentTree)
          if (matcher.findAt(currentTree)) {
            blocks += "$R"+ i + " " + currentTree.value()
            added = true
          }
        }
        if (!added) blocks += currentTree.value()
        traverseTree(currentTree, patterns, blocks)
      }
    }
  }

  def saveWord2VecTrainingFile(saveLoc: String): Unit = {
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    val conf: Config = ConfigFactory.load()

    implicit val system = ActorSystem("reactive-tweets", conf)
    implicit val materializer = ActorFlowMaterializer()

    import system.dispatcher

    //need to keep the id column and state label
    val word2VecFlow: Flow[NormalRow, Seq[Any], Unit] = Flow[NormalRow].mapAsync[Seq[Any]] { row =>
      scala.concurrent.Future {
        val tree = Tree.valueOf(struct.getTargetValue(row).get)

        val result = ArrayBuffer.empty[String]
        traverseTree(tree, patterns, result)

        Timer.completeOne()
        Seq(result.mkString(" "))
      }
    }

    val tweets: Source[NormalRow, Unit] = Source(() => data.strip)

    val printSink = Sink.foreach[Seq[Any]](line => output.writeRow(line))

    val g = FlowGraph.closed(printSink) { implicit builder: FlowGraph.Builder =>
      printsink =>

        import FlowGraph.Implicits._

        val bcast = builder.add(Balance[NormalRow](15))
        val merge = builder.add(Merge[Seq[Any]](15))

        tweets ~> bcast.in

        0 to 14 foreach { i =>
          bcast.out(i) ~> word2VecFlow ~> merge
        }

        merge ~> printsink
    }

    val materialized = g.run()

    materialized.onComplete(e => system.shutdown())
  }

}
