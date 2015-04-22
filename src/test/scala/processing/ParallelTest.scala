//package processing
//
//import akka.stream.ActorFlowMaterializer
//import akka.stream.scaladsl._
//import utils.ActorSystem
//import scala.concurrent.ExecutionContext.Implicits.global
//
//import scala.collection.mutable.ArrayBuffer
//import scala.concurrent.Future
//
///**
// * Created by anie on 4/2/2015
// */
//object ParallelTest extends App with ActorSystem {
//
//  implicit val materializer = ActorFlowMaterializer()
//  val source: Source[IntermediateResult, Unit] = Source(1 to 10)
//  val printSink = Sink.foreach[IntermediateResult](e => println(e))
//
//  type IntermediateResult = (Int, Option[Int])
//
//  val actionStream: ArrayBuffer[(IntermediateResult) => IntermediateResult] = ArrayBuffer.empty[(IntermediateResult) => IntermediateResult]
//
//  def add(a: Int): (Int, Option[Int]) = (a, Some(a + 1))
//
//  def exec(): Unit = {
//    val sourceReady = actionStream.drop(1).foldLeft(source)
//    {(source, action) =>
//      source.via(Flow[IntermediateResult].mapAsync(e => Future{action.apply(e)}))
//    }
//    sourceReady.runWith(printSink)
//  }
//
////    actionStream += ((a: Int) => add(a))
////    actionStream += ((a: Int) => add(a))
//
//    exec()
//
//}
