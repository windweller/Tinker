package macros

import org.scalatest.FlatSpec
import macros.Mappable._

/**
 * Created by Aimingnie on 8/28/15.
 */
class Mappable$Test extends FlatSpec {

  "to map" should "work" in {

    case class Item(name_this: String, price: Double)

//    val map = mapify(Item("lunch", 15.5))
//    println(map)

    val item = materialize[Item](Map("name" -> "dinner", "price" -> 25.8))

    println(item.name_this) // "dinner"
    println(item.price) // 25.8
  }
}
