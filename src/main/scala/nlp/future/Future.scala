package nlp.future

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import com.github.tototoshi.csv.CSVWriter
import com.typesafe.config.{ConfigFactory, Config}
import edu.stanford.nlp.trees.tregex.TregexPattern
import edu.stanford.nlp.trees.Tree
import newFiles.DataContainer
import newFiles.RowTypes.NormalRow
import newFiles.structure.DataStructure
import nlp.matcher.Matcher
import nlp.matcher.impl.Tregex
import nlp.parser.Parser
import nlp.parser.impl.StanfordPCFG
import FutureRules._
import utils.Timer
import scala.concurrent.Future

/**
 * Created by anie on 4/26/2015.
 */
class Future(val data: DataContainer, val struct: DataStructure, val patternRaw: Iterator[String]) {

  val parser = new Parser with StanfordPCFG
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
          println("processing: " + row(struct.getTarget.get))
          (struct.getIdValue(row).get, row(struct.getTarget.get),parser.parse(row(struct.getTarget.get)))
        })

    val tregexMatchFlow: Flow[(String, String, Tree), Seq[Any], Unit] = Flow[(String, String, Tree)].mapAsync[Seq[Any]] { tuple =>
      Future {
        val array = matcher.search(tuple._3, patterns)
        Timer.completeOne()
        Seq(tuple._1, tuple._2) ++ array.toSeq
      }
    }

    val printSink = Sink.foreach[Seq[Any]](line => output.writeRow(line))

    val tweets: Source[NormalRow, Unit] = Source(() => data.unify)
    val sourceReady = tweets.via(parseFlow).via(tregexMatchFlow)
    val materialized = sourceReady.runWith(printSink)

  }

}
