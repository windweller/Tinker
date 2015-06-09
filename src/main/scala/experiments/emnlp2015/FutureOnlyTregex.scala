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

import scala.concurrent.Future

/**
 * Created by anie on 6/8/2015.
 */
class FutureOnlyTregex(val data: DataContainer, val struct: DataStructure, val patternRaw: Iterator[String]) {
  val parser = new Parser
  val matcher = new Matcher with Tregex
  val patterns = patternRaw.map(e => TregexPattern.compile(e)).toList

  def saveFutureMatching(saveLoc: String): Unit = {
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    val conf: Config = ConfigFactory.load()
    implicit val system = ActorSystem("reactive-tweets", conf)
    implicit val materializer = ActorFlowMaterializer()

    import system.dispatcher

    //need to keep the id column and state label
    val tregexMatchFlow: Flow[NormalRow, Seq[Any], Unit] = Flow[NormalRow].mapAsync[Seq[Any]] { row =>
      Future {
        val array = matcher.search(Tree.valueOf(struct.getTargetValue(row).get), patterns)
        Timer.completeOne()
        Seq(struct.getIdValue(row).get) ++ struct.getKeepColumnsValue(row).get ++ array.toSeq
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
