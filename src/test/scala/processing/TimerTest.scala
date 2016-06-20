package processing

import org.scalatest.FlatSpec
import utils.Timer

/**
  * Created by aurore on 24/05/16.
  */
class TimerTest extends FlatSpec {
  "print Timer" should "work" in {
    var i = 0
    while (i < 800) {
      Timer.completeOne()
      i+=1
    }
  }
}
