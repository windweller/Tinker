package experiments.emnlp2015

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import com.github.tototoshi.csv.CSVWriter
import com.typesafe.config.{ConfigFactory, Config}
import edu.stanford.nlp.trees.Tree
import files.DataContainer
import files.RowTypes._
import files.structure.DataStructure
import nlp.parser.Parser
import utils.Timer

/**
 * Created by anie on 7/14/2015.
 */
class FutureParse(val data: DataContainer, val struct: DataStructure) {

  val parser = new Parser

  def saveFutureMatching(saveLoc: String): Unit = {
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    //comment out during regular task
    output.writeRow(Seq("SentenceID", "Sentence", "Parse"))

    val conf: Config = ConfigFactory.load()
    implicit val system = ActorSystem("reactive-tweets", conf)
    implicit val materializer = ActorFlowMaterializer()

    import system.dispatcher

    val parseFlow: Flow[NormalRow, Seq[Any], Unit] =
      Flow[NormalRow].mapAsync[Seq[Any]](row => scala.concurrent.Future {
        Seq(struct.getIdValue(row).get, parser.parse(row(struct.target.get)).toString) //struct.getIdValue(row).get,
      })

    val tweets: Source[NormalRow, Unit] = Source(() => data.strip)

    val printSink = Sink.foreach[Seq[Any]](line => output.writeRow(line))


    val g = FlowGraph.closed(printSink) { implicit builder: FlowGraph.Builder =>
      printsink =>

        import FlowGraph.Implicits._

        val bcast = builder.add(Balance[NormalRow](15))
        val merge = builder.add(Merge[Seq[Any]](15))

        tweets ~> bcast.in

        0 to 14 foreach  { i =>
          bcast.out(i) ~> parseFlow ~> merge
        }

        merge ~> printsink
    }

    val materialized = g.run()

    materialized.onComplete(e => system.shutdown())
  }

}
