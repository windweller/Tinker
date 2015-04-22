package newFiles.filetypes.space

import newFiles.filetypes.Doc

/**
 * Created by anie on 4/21/2015.
 */
trait Space  extends Doc {

  override val typesuffix: Vector[String] = Vector("txt", "prn")

  override def parse: (String) => Vector[String] = (line: String) => line.split(" ").toVector
}
