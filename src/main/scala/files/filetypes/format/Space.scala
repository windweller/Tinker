package files.filetypes.format

import files.filetypes.Doc

/**
 * Created by anie on 4/21/2015.
 */
trait Space  extends Doc {

  def typesuffix: Vector[String] = Vector("txt", "prn")

  def parse: (String) => Vector[String] = (line: String) => line.split(" ").toVector
}
