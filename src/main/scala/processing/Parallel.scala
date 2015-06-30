package processing

import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import files.RowTypes._
import utils.ActorSys


/**
 * Created by anie on 4/21/2015.
 */
trait Parallel extends Operation with ActorSys {

  //no need to clean scheduler's opSequence once exec() is done
  //right now parallel follows
  def exec(): Unit = {

    val rows = opSequence.pop()
    implicit val materializer = ActorFlowMaterializer()

    import system.dispatcher

    val source: Source[NormalRow, Unit] = Source(() => rows)
    val sink = Sink.foreach[NormalRow](row => bufferWrite(row))

    val g = FlowGraph.closed(sink) { implicit builder: FlowGraph.Builder =>
      sink =>

        import FlowGraph.Implicits._

        val bcast = builder.add(Balance[NormalRow](workerNum.get))
        val merge = builder.add(Merge[NormalRow](workerNum.get))

        source ~> bcast.in

        0 to workerNum.get foreach  { i =>
          bcast.out(i) ~> merge
        }

        merge ~> sink
    }

    val materialized = g.run()
    materialized.onComplete(e => system.shutdown())
  }

}