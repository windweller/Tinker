package processing

import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import core.TypedRow
import core.structure.DataSelect
import utils.ActorSys._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait Sequential extends Operation {

  override def exec(select: Option[DataSelect] = None, ignore: Option[DataSelect] = None): Unit = {
    val rows = opSequence.top
    implicit val materializer = ActorFlowMaterializer()

    val source: Source[TypedRow, Unit] = Source(() => rows)
    val sink = Sink.foreach[TypedRow](row => bufferWrite(row, select, ignore))

    //pipelining sequential actions
    val g = FlowGraph.closed(sink) { implicit builder: FlowGraph.Builder =>
      sink =>

        import FlowGraph.Implicits._

        val withoutSink = source via graphFlows.fold(Flow[TypedRow])(_ via _)

        val runnableFlow = withoutSink ~> sink
    }

    val materialized = g.run()

    materialized.onComplete {
      case Success(b) =>
        bufferClose()
        system.shutdown()
      case Failure(e) =>
        println(s"Error message: ${e.getMessage}")
        println(s"Error stack: ${e.printStackTrace()}")
        bufferClose()
        system.shutdown()
    }
  }
}
