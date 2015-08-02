package utils

import processing.Scheduler

import scala.collection.mutable
import scala.language.implicitConversions

/**
 * Created by anie on 3/25/2015
 */
object ParameterCallToOption {

  object Implicits {
    implicit def boolean2Option(b: Boolean): Option[Boolean] = Some(b)
    implicit def string2Option(s: String): Option[String] = Some(s)
    implicit def map2Option(map: Map[String, Int]): Option[Map[String, Int]] = Some(map)
    implicit def indexedSeqInt2Option(indexedSeq: IndexedSeq[Int]): Option[IndexedSeq[Int]] = Some(indexedSeq)
    implicit def int2Option(i: Int):  Option[Int] = Some(i)
    implicit def indexedSeqString2Option(indexedSeq: IndexedSeq[String]): Option[IndexedSeq[String]] = Some(indexedSeq)
    implicit def scheduler2Option(scheduler: Scheduler): Option[Scheduler] = Some(scheduler)
    implicit def listString2Option(list: List[String]): Option[List[String]] = Some(list)
    implicit def hashmap2Option(map: mutable.HashMap[String, Int]): Option[mutable.HashMap[String, Int]] = Some(map)
  }
}

object OptionToParameter {

  object Implicits {
    implicit def option2Boolean(b: Option[Boolean]): Boolean = b.get
    implicit def option2String(b: Option[String]): String = b.get
  }

}