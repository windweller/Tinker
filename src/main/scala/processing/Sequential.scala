package processing

import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import files.RowTypes._
import files.structure.DataStructure
import utils.ActorSys._
import scala.concurrent.ExecutionContext.Implicits.global

//TODO: this is not working
trait Sequential extends Operation {

  def exec(struct: Option[DataStructure] = None): Unit = {
    val rows = opSequence.top
    implicit val materializer = ActorFlowMaterializer()

    val source: Source[NormalRow, Unit] = Source(() => rows)
    val sink = Sink.foreach[NormalRow](row => bufferWrite(row, struct))

    val g = FlowGraph.closed(sink) { implicit builder: FlowGraph.Builder =>
      sink =>

        import FlowGraph.Implicits._

        graphFlows.foreach { flow =>
          source ~> flow
        }

    }

    val materialized = g.run()

    materialized.onComplete{ e =>
      bufferClose()
      system.shutdown()
    }

//    rows.foreach(row => bufferWrite(row, struct))
//    bufferClose()
  }
}
