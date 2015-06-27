package processing

import utils.ActorSystem
import akka.stream.scaladsl.FlowGraph.Implicits._

/**
 * Created by anie on 4/21/2015.
 */
trait Parallel extends Operation with ActorSystem {

  //no need to clean scheduler's opSequence once exec() is done
  //right now parallel follows
  def exec(): Unit = {

  }

}