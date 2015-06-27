package files.filetypes.format

import files.filetypes.Doc

/**
 * Created by anie on 4/18/2015
 */
trait Tab extends Doc {

  def typesuffix: Vector[String] = Vector("tab", "txt")

  def parse: (String) => Vector[String] = (line: String) => line.split("\t").toVector

}
