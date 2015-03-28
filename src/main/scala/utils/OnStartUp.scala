package utils

import akka.actor.ActorSystem

/**
 * Created by anie on 3/26/2015.
 * contains a universal actor system
 */
object OnStartUp {

  implicit val system = ActorSystem("Tinker-parallel-processing")

}
