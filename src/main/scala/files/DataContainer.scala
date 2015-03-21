package files

import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 3/21/2015.
 */
trait DataContainer {
  def iterator: Either[Iterator[Array[String]], Iterator[Map[String, String]]]
  val data: ArrayBuffer[Array[String]]
}
