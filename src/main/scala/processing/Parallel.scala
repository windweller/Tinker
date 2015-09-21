package processing

import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import core.TypedRow
import core.structure.{DataSelect, DataStruct}
import utils.ActorSys._

import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by anie on 4/21/2015
 */
trait Parallel extends Operation {

  //no need to clean scheduler's opSequence once exec() is done
  //right now parallel follows
  override def exec(select: Option[DataSelect] = None, ignore: Option[DataSelect] = None): Unit = {

    val rows = opSequence.top
    implicit val materializer = ActorFlowMaterializer()

    val source: Source[TypedRow, Unit] = Source(() => rows)
    val sink = Sink.foreach[TypedRow](row => bufferWrite(row, select, ignore))

    val g = FlowGraph.closed(sink) { implicit builder: FlowGraph.Builder =>
      sink =>

        import FlowGraph.Implicits._

        val bcast = builder.add(Balance[TypedRow](workerNum.get))
        val merge = builder.add(Merge[TypedRow](workerNum.get))

        source ~> bcast.in

        0 to workerNum.get - 1 foreach  { i =>
          //adding custom flow after the fanning out
          val withoutSink = bcast.out(i) ~> graphFlows.fold(Flow[TypedRow])(_ via _)
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