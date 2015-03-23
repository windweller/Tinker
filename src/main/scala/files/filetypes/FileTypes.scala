package files.filetypes

import utils.FailureHandle

import scala.concurrent.Future

/**
 * Created by anie on 3/23/2015.
 */
trait FileTypes extends FailureHandle{

  val headerMap: Option[Array[String]]

  def parse: (String) => Array[String] = (line: String) => Array(line)

  def parseWithHeader: (String) => Map[String, String] = {
    (nextLine: String) => {
      if (headerMap.nonEmpty)
        Map(headerMap.get.zip(parse(nextLine)): _*)
      else {fatal("Cannot invoke header parse for a file with no header"); throw new Exception}
    }
  }

  def save(data: Vector[Array[String]], outputFile: String): Future[Unit] = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }
}
