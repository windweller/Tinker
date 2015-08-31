package macros

import macros.Mappable._

/**
 * Created by Aimingnie on 8/28/15.
 */
object Mappable$Test extends App {

    case class Item(name_this: String, price: Double)

    def materialize[T: Mappable](map: Map[String, Any]) = implicitly[Mappable[T]].fromMap(map)

    def mapify[T: Mappable](t: T) = implicitly[Mappable[T]].toMap(t)

//    val map = mapify(Item("lunch", 15.5))
//    println(map)

    val item = materialize[Item](Map("name_this" -> "dinner", "price" -> 25.8))

    println(item.name_this) // "dinner"
    println(item.price) // 25.8

}
