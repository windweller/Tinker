package processing.buffers

import java.nio.file.Path

import files.DataContainer
import files.DataContainerTypes._
import output.Output
import utils.FailureHandle

import scala.concurrent.Future

/**
 * Created by anie on 4/3/2015
 *
 * This is the higher-level Buffer module
 * lower level implementation includes
 * FileBuffer, DBBuffer and so forth
 *
 * We di
 */
trait Buffer extends Output with FailureHandle {

  val dataContainer: DataContainer

  def getSaveLoc(outputFile: Option[String] = None,
                 outputOverride: Boolean = false): Option[Path] = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }

}
