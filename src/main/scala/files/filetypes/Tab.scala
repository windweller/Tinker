package files.filetypes

import files.Doc

import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 3/19/2015.
 */
class TabFile(f: String, header: Boolean, rawData: Option[ArrayBuffer[Array[String]]]) extends Doc {

  lazy val data = if (rawData.isEmpty) processTab(f, header) else rawData.get

  def processTab(loc: String, header: Boolean): ArrayBuffer[Array[String]] = {
    readFile[Array](loc, header, (line) => line.split("\t"))
  }

  override def save(loc: String): Unit = ???

  override def iterator = ???

}

object TabFile {

}
