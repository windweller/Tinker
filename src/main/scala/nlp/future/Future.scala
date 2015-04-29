package nlp.future

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import com.github.tototoshi.csv.CSVWriter
import com.typesafe.config.{ConfigFactory, Config}
import edu.stanford.nlp.trees.tregex.TregexPattern
import edu.stanford.nlp.trees.Tree
import newFiles.DataContainer
import newFiles.rowTypes.NormalRow
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
class Future(val data: DataContainer, val struct: DataStructure) {

  val parser = new Parser with StanfordPCFG
  val matcher = new Matcher with Tregex

  val futureCompiledPattern = for (pattern <- patternFuture) yield TregexPattern.compile(pattern)
  val pastCompiledPattern = for (pattern <- patternsPast) yield TregexPattern.compile(pattern)
  val presentCompiledPattern = for (pattern <- patternPresent) yield TregexPattern.compile(pattern)

  def saveFutureMatching(saveLoc: String): Unit = {
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    val it = data.iterators.head

    val conf: Config = ConfigFactory.load()
    implicit val system = ActorSystem("reactive-tweets", conf)
    implicit val materializer = ActorFlowMaterializer()

    import system.dispatcher

    val parseFlow: Flow[NormalRow, (String, String, Tree), Unit] =
        Flow[NormalRow].mapAsync[(String, String, Tree)](row => Future{
          println("processing: " + row(struct.getTarget))
          (struct.getId(row).get, row(struct.getTarget),parser.parse(row(struct.getTarget)))
        })

    val tregexMatchFlow: Flow[(String, String, Tree), Seq[Any], Unit] = Flow[(String, String, Tree)].mapAsync[Seq[Any]] { tuple =>
      Future {
        val array = matcher.search(tuple._3, futureCompiledPattern) ++
          matcher.search(tuple._3, pastCompiledPattern) ++
          matcher.search(tuple._3, presentCompiledPattern)
        Timer.completeOne()
        Seq(tuple._1, tuple._2) ++ array.toSeq
      }
    }

    val printSink = Sink.foreach[Seq[Any]](line => output.writeRow(line))

    it.foreach { group =>
      println("in here!")
      val tweets: Source[NormalRow, Unit] = Source(() => group._2)
      val sourceReady = tweets.via(parseFlow).via(tregexMatchFlow)
      val materialized = sourceReady.runWith(printSink)
    }
  }

}
