package experiments.emnlp2015

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import com.github.tototoshi.csv.CSVWriter
import com.typesafe.config.{Config, ConfigFactory}
import core.{DataContainer, RowTypes}
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import RowTypes._
import core.structure.DataStructure
import nlp.matcher.Matcher
import nlp.matcher.impl.Tregex
import nlp.parser.Parser
import utils.Timer

import scala.collection.mutable.ListBuffer

/**
 * Created by anie on 6/7/2015.
 */
class Future(val data: DataContainer, val struct: DataStructure, val patternRaw: List[String],
             val tndoc: Option[String] = None, val tcdoc: Option[String] = None,
              val mispelling: Boolean = false) {

  val parser = new Parser
  val matcher = new Matcher with Tregex
  //pre-processing patterns (replace TN|TC)
  val tnterms = tndoc.map(doc => scala.io.Source.fromFile(doc).getLines().toList)
  val tcterms = tcdoc.map(doc => scala.io.Source.fromFile(doc).getLines().toList)

  val patterns = if (tndoc.nonEmpty || tcdoc.nonEmpty) preprocessTregex(patternRaw).map(e => TregexPattern.compile(e))
                 else  patternRaw.map(e => TregexPattern.compile(e))

  def preprocessTregex(patterns: List[String]): List[String] = {
    val result = ListBuffer.empty[String]
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
    result.toList
  }

  def saveFutureMatching(saveLoc: String): Unit = {
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    //comment out during regular task
    output.writeRow(Seq("ID", "Sentence", "Parse") ++ preprocessTregex(patternRaw))

    val conf: Config = ConfigFactory.load()
    implicit val system = ActorSystem("reactive-tweets", conf)
    implicit val materializer = ActorFlowMaterializer()

    import system.dispatcher

    val parseFlow: Flow[NormalRow, (NormalRow, Tree), Unit] =
      Flow[NormalRow].mapAsync[(NormalRow, Tree)](row => scala.concurrent.Future {
//        println("processing: " + row(struct.target.get)) //no need to print
        (row, parser.parse(row(struct.target.get))) //struct.getIdValue(row).get,
      })

    val tregexMatchFlow: Flow[(NormalRow, Tree), Seq[Any], Unit] = Flow[(NormalRow, Tree)].mapAsync[Seq[Any]] { tuple =>
      scala.concurrent.Future {
        val array = matcher.search(tuple._2, patterns)
        Timer.completeOne()
        Seq(struct.getIdValue(tuple._1).get, struct.getTargetValue(tuple._1).get, tuple._2.toString) ++ array.toSeq
      }
    }

    val tweets: Source[NormalRow, Unit] = Source(() => data.strip)

    val printSink = Sink.foreach[Seq[Any]](line => output.writeRow(line))


    val g = FlowGraph.closed(printSink) { implicit builder: FlowGraph.Builder =>
      printsink =>

      import FlowGraph.Implicits._

      val bcast = builder.add(Balance[NormalRow](20))
        val merge = builder.add(Merge[Seq[Any]](20))

      tweets ~> bcast.in

        0 to 19 foreach  { i =>
          bcast.out(i) ~> parseFlow ~> tregexMatchFlow ~> merge
        }

        merge ~> printsink
    }

    val materialized = g.run()

    materialized.onComplete(e => system.shutdown())

  }
}
