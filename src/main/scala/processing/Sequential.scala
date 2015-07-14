package processing

import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import files.RowTypes._
import files.structure.DataStructure
import utils.ActorSys
import scala.concurrent.ExecutionContext.Implicits.global

trait Sequential extends Operation with ActorSys {

  def exec(struct: Option[DataStructure] = None): Unit = {
    val rows = opSequence.top
    implicit val materializer = ActorFlowMaterializer()

    val source: Source[NormalRow, Unit] = Source(() => rows)
    val sink = Sink.foreach[NormalRow](row => bufferWrite(row, struct))

    //pipelining sequential actions
    val g = FlowGraph.closed(sink) { implicit builder: FlowGraph.Builder =>
      sink =>

        import FlowGraph.Implicits._

        val withoutSink = source via graphFlows.fold(Flow[NormalRow])(_ via _)

        val runnableFlow = withoutSink ~> sink
    }

    val materialized = g.run()

    materialized.onComplete{ e =>
      bufferClose()
      system.shutdown()
    }
  }
}
