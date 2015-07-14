package utils

import akka.actor.ActorSystem
import com.typesafe.config.{ConfigFactory, Config}

/**
 * Created by Aimingnie on 6/30/15.
 */
object ActorSys {

  val conf: Config = ConfigFactory.load()
  implicit val system = ActorSystem("Tinker-parallel-processing", conf)

}
