package files.structure

import scala.language.implicitConversions

/**
 * Created by anie on 7/15/2015.
 */

case class Index(i: Option[Int], s: Option[String]) extends StructureUtils {
  def to(special: Index.Value): (Index, Index.Value) = (this, special)
  lazy val get: Option[String] = getSingleIntStringOption(i, s)
}

//something to think about, that will support DSL like
//5 to last
object Index extends Enumeration {
  val last = Value

  type IndexRange = (Index, Index.Value)

  implicit def fromInt(i: Int): Index = new Index(Some(i), None)
  implicit def fromString(s: String): Index = new Index(None, Some(s))
}
