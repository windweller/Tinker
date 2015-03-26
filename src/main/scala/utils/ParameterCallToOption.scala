package utils

import files.DataContainer

import scala.language.implicitConversions

/**
 * Created by anie on 3/25/2015.
 */
object ParameterCallToOption {

  object implicits {
    implicit def string2Option(s: String): Option[String] = Some(s)
    implicit def map2Option(map: Map[String, Int]): Option[Map[String, Int]] = Some(map)
    implicit def indexedSeq2Option(indexedSeq: IndexedSeq[Int]): Option[IndexedSeq[Int]] = Some(indexedSeq)
    implicit def int2Option(i: Int):  Option[Int] = Some(i)
  }

}
