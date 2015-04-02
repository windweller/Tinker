package utils

import akka.actor.ActorSystem

/**
 * Created by anie on 4/1/2015.
 */
trait ActorSystem {
  implicit val system = ActorSystem("Tinker-parallel-processing")
}
