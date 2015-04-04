package processing

import java.nio.file.Path

import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import files.DataContainerTypes._
import OperationType._
import utils.{ActorSystem, FailureHandle}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by anie on 3/23/2015.
 *
 * This will take/consume a function (match() or process()) that
 * ParseImpl will provide
 *
 * Even though using barebone Future could be easy
 * It does not have the "backpressure" we want, so
 * it might spin into a disaster
 *
 * This is a function/implementation agnostic
 * concurrency model
 */
trait Parallel extends Operation with FailureHandle with ActorSystem {

  protected implicit val materializer = ActorFlowMaterializer()

  protected val source: Source[NormalRow, Unit] = Source(() => dataContainer.dataIterator)

  implicit var saveLoc: Option[Path] = None

  //save it to specified location or temp file
  val printSink = Sink.foreach[IntermediateResult](e => save(combine(e._1, e._2)))

  def exec(outputFile: Option[String] = None,
           outputOverride: Boolean = false): Unit = {
    if (actionStream.size == 0) fatal("cannot call exec() when there is no action defined")

    saveLoc = getSaveLoc(outputFile, outputOverride)

    val sourceReady = if (actionStream.size == 1)
                          source
                          .via(Flow[NormalRow]
                          .mapAsync(e => {println("inside here"); applyHeadFlow(e, actionStream.head)}))
                      else
                         actionStream.drop(1).foldLeft(source
                           .via(Flow[NormalRow]
                           .mapAsync(e =>applyHeadFlow(e, actionStream.head)))
                         ){(source, action) =>
                          source.via(Flow[IntermediateResult].mapAsync(e => Future(action(e))))
    }
    sourceReady.runWith(printSink)
  }

  //this is an open access function
  //users can use it when they write their custom Akka Stream flows
  def applyHeadFlow(row: NormalRow, action: (IntermediateResult) => IntermediateResult): Future[IntermediateResult] = Future {
    action.apply(row, None)
  }

}
