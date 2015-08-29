package macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
/**
 * Created by anie on 8/27/2015
 */
object Mappable {

  def materialize[T: Mappable](map: Map[String, Any]) = implicitly[Mappable[T]].fromMap(map)

  def mapify[T: Mappable](t: T) = implicitly[Mappable[T]].toMap(t)

  implicit def materializeMappable[T]: Mappable[T] = macro materializeMappableImpl[T]

  def materializeMappableImpl[T:c.WeakTypeTag](c: blackbox.Context): c.Expr[Mappable[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]

    val declarations = tpe.decls
    val ctor = declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.get

    val params = ctor.paramLists.head

    val toMapParams = params.map { field =>
      val name = field.name.toTermName
      val mapKey = name.decoded
      q"$mapKey -> t.$name"
    }

    val companion = tpe.typeSymbol.companion

    val fromMapParams = params.map { field =>
      val name = field.name.toTermName
      val decoded = name.decoded
      val returnType = tpe.decl(name).typeSignature
      q"map($decoded).asInstanceOf[$returnType]"
    }

    c.Expr[Mappable[T]] { q"""
      new Mappable[$tpe] {
        def toMap(t: $tpe) = Map(..$toMapParams)
        def fromMap(map: Map[String, Any]) = ???
      }
    """
    }

  }

}

trait Mappable[T] {
  def toMap(t: T): Map[String, Any]
  def fromMap(map: Map[String, Any]): T
}