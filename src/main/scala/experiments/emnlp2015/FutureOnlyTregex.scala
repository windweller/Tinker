package experiments.emnlp2015

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import com.github.tototoshi.csv.CSVWriter
import com.typesafe.config.{ConfigFactory, Config}
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import newFiles.DataContainer
import newFiles.RowTypes._
import newFiles.structure.DataStructure
import nlp.matcher.Matcher
import nlp.matcher.impl.Tregex
import nlp.parser.Parser
import utils.Timer

import scala.collection.mutable.ListBuffer

/**
 * Created by anie on 6/8/2015.
 */
class FutureOnlyTregex(val data: DataContainer, val struct: DataStructure, val patternRaw: List[String],
                                      val tndoc: Option[String] = None, val tcdoc: Option[String] = None) {

  import scala.concurrent.Future

  val parser = new Parser
  val matcher = new Matcher with Tregex

  //pre-processing patterns (replace TN|TC)
  val tnterms = tndoc.map(doc => scala.io.Source.fromFile(doc).getLines().toList)
  val tcterms = tcdoc.map(doc => scala.io.Source.fromFile(doc).getLines().toList)

  val patterns = if (tndoc.nonEmpty && tcdoc.nonEmpty)
                    preprocessTregex(patternRaw).map(e => TregexPattern.compile(e))
                 else  patternRaw.map(e => TregexPattern.compile(e))

  def preprocessTregex(patterns: List[String]): List[String] = {
    val result = ListBuffer.empty[String]
    patterns.foreach { p =>
      if (p.contains("TN|TC")) {
        //just replace all TN and TC
        result ++= tnterms.get.map(tn => p.replace("TN|TC", tn))
        result ++= tcterms.get.map(tc => p.replace("TN|TC", tc))
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
    output.writeRow(Seq("sentence", "label") ++ patternRaw)

    val conf: Config = ConfigFactory.load()
    implicit val system = ActorSystem("reactive-tweets", conf)
    implicit val materializer = ActorFlowMaterializer()

    import system.dispatcher

    //need to keep the id column and state label
    val tregexMatchFlow: Flow[NormalRow, Seq[Any], Unit] = Flow[NormalRow].mapAsync[Seq[Any]] { row =>
      scala.concurrent.Future {
        val tree = Tree.valueOf(struct.getTargetValue(row).get)
        val array = matcher.search(tree, patterns)
        val treeListIt = tree.getLeaves.iterator()
        var result = ""
        while (treeListIt.hasNext) {
          result += treeListIt.next()
          result += " "
        }
        Timer.completeOne()

        Seq(result) ++ struct.getKeepColumnsValue(row).get ++ array.toSeq
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
          bcast.out(i) ~> tregexMatchFlow ~> merge
        }

        merge ~> printsink
    }.run()
  }

}
