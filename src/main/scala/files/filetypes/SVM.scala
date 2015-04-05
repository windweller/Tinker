package files.filetypes

import java.nio.file.Path

/**
 * Created by anie on 4/5/2015
 *
 * This is to read in and write out
 * condensed vector:
 * 125:1 130:1
 *
 * Right now it only has write functionality
 */
trait SVM extends FileTypes {

  override def save(row: Vector[String])(implicit file: Option[Path]): Unit = {

  }

}
