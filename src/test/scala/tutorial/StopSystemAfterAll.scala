package tutorial

import akka.testkit.TestKit
import org.scalatest.{Suite, BeforeAndAfterAll}

/**
 * Created by anie on 7/14/2015.
 */
trait StopSystemAfterAll extends  BeforeAndAfterAll {

  this: TestKit with Suite =>

  override def afterAll(): Unit = {
    super.afterAll()
    system.shutdown()
  }

}
