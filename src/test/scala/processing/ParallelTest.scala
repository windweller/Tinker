package processing

import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import org.scalatest.FlatSpec
import utils.ActorSystem
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

/**
 * Created by anie on 4/2/2015.
 */
object ParallelTest extends App with ActorSystem {

  implicit val materializer = ActorFlowMaterializer()
  val source: Source[Int, Unit] = Source(1 to 10)
  val printSink = Sink.foreach[Int](e => println(e))

  val actionStream: ArrayBuffer[(Int) => Int] = ArrayBuffer.empty[(Int) => Int]

  def add(a: Int) = a + 1

  def exec(): Unit = {
    val sourceReady = actionStream.drop(1).foldLeft(source)
    {(source, action) =>
      source.via(Flow[Int].mapAsync(e => Future{action.apply(e)}))
    }

    sourceReady
    sourceReady.runWith(printSink)
  }

    actionStream += ((a: Int) => add(a))
    actionStream += ((a: Int) => add(a))

    exec()

}
