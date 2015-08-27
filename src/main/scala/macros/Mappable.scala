package macros

import scala.reflect.macros.blackbox
/**
 * Created by anie on 8/27/2015.
 */
object Mappable {

//  def materializeMappableImpl[T:c.WeakTypeTag](c: blackbox.Context): c.Expr[Mappable[T]] = {
//    import c.universe._
//    val tpe = weakTypeOf[T]
//  }

}

trait Mappable[T] {
  def toMap(t: T): Map[String, Any]
  def fromMap(map: Map[String, Any]): T
}