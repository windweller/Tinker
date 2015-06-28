package nlp.future

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import com.github.tototoshi.csv.CSVWriter
import com.typesafe.config.{Config, ConfigFactory}
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.DataContainer
import files.RowTypes.NormalRow
import files.structure.DataStructure
import nlp.matcher.Matcher
import nlp.matcher.impl.Tregex
import nlp.parser.Parser
import utils.Timer

/**
 * Created by anie on 4/26/2015.
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
        Flow[NormalRow].mapAsync[(String, String, Tree)](row => scala.concurrent.Future{
          println("processing: " + row(struct.target.get))
          (struct.getIdValue(row).get, row(struct.target.get),parser.parse(row(struct.target.get)))
        })

    val tregexMatchFlow: Flow[(String, String, Tree), Seq[Any], Unit] = Flow[(String, String, Tree)].mapAsync[Seq[Any]] { tuple =>
      scala.concurrent.Future {
        val array = matcher.search(tuple._3, patterns)
        Timer.completeOne()
        Seq(tuple._1, tuple._2) ++ array.toSeq
      }
    }

    val printSink = Sink.foreach[Seq[Any]](line => output.writeRow(line))

    val tweets: Source[NormalRow, Unit] = Source(() => data.strip)
    val sourceReady = tweets.via(parseFlow).via(tregexMatchFlow)
    val materialized = sourceReady.runWith(printSink)

  }

}
