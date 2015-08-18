package processing

import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import core.RowTypes
import RowTypes._
import core.structure.DataStructure
import utils.ActorSys._
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by anie on 4/21/2015
 */
trait Parallel extends Operation {

  //no need to clean scheduler's opSequence once exec() is done
  //right now parallel follows
  override def exec(struct: Option[DataStructure] = None): Unit = {

    val rows = opSequence.top
    implicit val materializer = ActorFlowMaterializer()

    val source: Source[NormalRow, Unit] = Source(() => rows)
    val sink = Sink.foreach[NormalRow](row => bufferWrite(row, struct))

    val g = FlowGraph.closed(sink) { implicit builder: FlowGraph.Builder =>
      sink =>

        import FlowGraph.Implicits._

        val bcast = builder.add(Balance[NormalRow](workerNum.get))
        val merge = builder.add(Merge[NormalRow](workerNum.get))

        source ~> bcast.in

        0 to workerNum.get - 1 foreach  { i =>
          //adding custom flow after the fanning out
          val withoutSink = bcast.out(i) ~> graphFlows.fold(Flow[NormalRow])(_ via _)
          withoutSink ~> merge
        }
        merge ~> sink
    }

    val materialized = g.run()

    materialized.onComplete{ e =>
      bufferClose()
      system.shutdown()
    }

  }

}