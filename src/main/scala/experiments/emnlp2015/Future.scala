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
 * Created by anie on 6/7/2015.
 */
class Future(val data: DataContainer, val struct: DataStructure, val patternRaw: Iterator[String]) {

  val parser = new Parser
  val matcher = new Matcher with Tregex
  val patterns = patternRaw.map(e => TregexPattern.compile(e)).toList

  def saveFutureMatching(saveLoc: String): Unit = {
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    val conf: Config = ConfigFactory.load()
    implicit val system = ActorSystem("reactive-tweets", conf)
    implicit val materializer = ActorFlowMaterializer()

    import system.dispatcher

    val parseFlow: Flow[NormalRow, (String, String, Tree), Unit] =
      Flow[NormalRow].mapAsync[(String, String, Tree)](row => Future{
        println("processing: " + row(struct.target.get))
        (struct.getIdValue(row).get, row(struct.target.get),parser.parse(row(struct.target.get)))
      })

    val tregexMatchFlow: Flow[(String, String, Tree), Seq[Any], Unit] = Flow[(String, String, Tree)].mapAsync[Seq[Any]] { tuple =>
      Future {
        val array = matcher.search(tuple._3, patterns)
        Timer.completeOne()
        Seq(tuple._1, tuple._2) ++ array.toSeq
      }
    }

    val tweets: Source[NormalRow, Unit] = Source(() => data.strip)

    val printSink = Sink.foreach[Seq[Any]](line => output.writeRow(line))


    val g = FlowGraph.closed(printSink) { implicit builder: FlowGraph.Builder =>
      printsink =>

      import FlowGraph.Implicits._

      val bcast = builder.add(Broadcast[NormalRow](10))
        val merge = builder.add(Merge[Seq[Any]](10))

      tweets ~> bcast.in

        0 to 9 foreach  { i =>
          bcast.out(i) ~> parseFlow ~> tregexMatchFlow ~> merge
        }

        merge ~> printsink
    }.run()


//    val sourceReady = tweets.via(parseFlow).via(tregexMatchFlow)
//    val materialized = sourceReady.runWith(printSink)

  }
}
