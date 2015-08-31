package macros

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

/**
 * Created by anie on 8/27/2015
 */
object Mappable {

  implicit def materializeMappable[T]: Mappable[T] = macro materializeMappableImpl[T]

  def materializeMappableImpl[T:c.WeakTypeTag](c: whitebox.Context): c.Tree = {
    import c.universe._
    val tpe = weakTypeOf[T]

    val declarations = tpe.decls
    val ctor = declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.get

    val params = ctor.paramLists.head

    val toMapParams = params.map { field =>
      val name = field.asTerm.name
      val mapKey = name.decodedName.toString
      q"$mapKey -> t.$name"
    }

    val companion = tpe.typeSymbol.companion

    val fromMapParams = params.map { field =>
      val name = field.asTerm.name
      val decoded = name.decodedName.toString
      val returnType = tpe.decl(name).typeSignature
      q"map($decoded).asInstanceOf[$returnType]"
    }

    q"""
      new Mappable[$tpe] {
        def toMap(t: $tpe) = Map(..$toMapParams)
        def fromMap(map: Map[String, Any]) = $companion(..$fromMapParams)
      }
    """
  }

}

trait Mappable[T] {
  def toMap(t: T): Map[String, Any]
  def fromMap(map: Map[String, Any]): T
}