package files

import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 3/20/2015.
 */
class Dir(f: String, header: Boolean) extends DataContainer{

  lazy val data: ArrayBuffer[Array[String]] = ArrayBuffer()

  def iterator: Either[Iterator[Array[String]], Iterator[Map[String, String]]] = ???

}

object Dir {

}