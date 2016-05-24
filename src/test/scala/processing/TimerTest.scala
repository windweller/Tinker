package processing

import org.scalatest.FlatSpec
import utils.Timer

/**
  * Created by aurore on 24/05/16.
  */
class TimerTest extends FlatSpec {
  "print Timer" should "work" in {
    while (true) {
      Timer.completeOne()
    }
  }
}
