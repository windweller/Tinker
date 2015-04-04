package baseModules

import java.nio.file.Path

import files.DataContainerTypes._
import utils.FailureHandle

import scala.concurrent.Future

/**
 * Created by anie on 4/4/2015
 *
 * This is a high level baseModules module interface
 * all baseModules-related module must inherit this one
 */
trait Output extends FailureHandle {

  def save(it: NormalRow)(implicit file: Option[Path]): Unit = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }

}
