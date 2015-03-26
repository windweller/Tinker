package files.filetypes

import utils.FailureHandle

import scala.concurrent.Future

/**
 * FileTypes include normal/generic file types
 * as well as special file types such as
 * MalletFileType or VarroSubtreeXML format
 */
trait FileTypes extends FailureHandle{

  val headerString: Option[Array[String]]
  val headerMap: Option[Map[String, Int]]

  protected def parse: (String) => Array[String] = (line: String) => Array(line)

  protected def parseWithHeader: (String) => Map[String, String] = {
    (nextLine: String) => {
      if (headerString.nonEmpty)
        Map(headerString.get.zip(parse(nextLine)): _*)
      else {fatal("Cannot invoke header parse for a file with no header"); throw new Exception}
    }
  }

  def save(data: Vector[Array[String]], outputFile: String): Future[Unit] = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }
}
