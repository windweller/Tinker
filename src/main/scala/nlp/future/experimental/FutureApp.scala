package nlp.future.experimental

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Source, Sink, Flow}
import com.github.tototoshi.csv.CSVWriter
import edu.stanford.nlp.trees.Tree
import files.DataContainer
import files.filetypes.format._
import files.RowTypes._
import files.filetypes.format.Tab
import files.structure.DataStructure
import files.structure.predefined.BasicNLP
import nlp.matcher.Matcher
import nlp.matcher.impl.Tregex
import nlp.parser.Parser
import edu.stanford.nlp.trees.tregex.TregexPattern
import utils.Timer
import utils.ParameterCallToOption.Implicits._
import scala.util.{Failure, Success}

/**
 * Created by anie on 5/12/2015
 */
object FutureApp extends App {

  val parser = new Parser
  val matcher = new Matcher with Tregex

  val patternRaw = io.Source.fromFile("E:\\Allen\\R\\acl2015\\FutureRules_2.0b.txt").getLines().filter(e => e != "").map(e => e.replaceFirst("\\s+$", ""))

  val patterns = patternRaw.map(e => TregexPattern.compile(e)).toList

  val output: CSVWriter = CSVWriter.open("E:\\Allen\\NYTFuture\\NYT_result_2.0b\\nyt_by_sen.txt", append = true)

  val struct = new DataStructure(idColumnWithName = "SentenceID", targetColumnWithName = "Parse", keepColumnsWithNames = Vector("ParagraphID", "PageID")) with BasicNLP
  val data = new DataContainer("E:\\Allen\\NYTFuture\\NYT", header = true) with Tab

  /** process **/

//  val conf: Config = ConfigFactory.load()
  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorFlowMaterializer()

  import system.dispatcher

  output.writeRow(Seq("id", "ParagraphID", "PageID") ++ patternRaw.toList)

  val tregexMatchFlow: Flow[NormalRow, Seq[Any], Unit] = Flow[NormalRow].map[Seq[Any]] { row =>
      println(struct.getIdValue(row).get)
      val array = matcher.search(Tree.valueOf(struct.getTargetValue(row).get), patterns)
      Timer.completeOne()
      Seq(struct.getIdValue(row).get) ++ struct.getKeepColumnsValue(row).get ++ array.toSeq
  }

  val printSink = Sink.foreach[Seq[Any]](line => output.writeRow(line))

  //merging
  val itr = data.strip

    val tweets: Source[NormalRow, Unit] = Source(() => itr)
    val sourceReady = tweets.via(tregexMatchFlow)
    val materialized = sourceReady.runWith(printSink)

    materialized.onComplete {
      case Success(_) => system.shutdown()
      case Failure(e) =>
        println(s"Failure: ${e.getMessage}")
        e.printStackTrace()
      system.shutdown()
    }


}
