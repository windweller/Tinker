package macros

import org.scalatest.FlatSpec
import macros.Mappable._

/**
 * Created by Aimingnie on 8/28/15.
 */
class Mappable$Test extends FlatSpec {

  "to map" should "work" in {

    case class Item(name_this: String, price: Double)

    val map = mapify(Item("lunch", 15.5))
    println(map)

  }
}
