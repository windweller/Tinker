package newFiles.filetypes.csv

import newFiles.RowTypes._
import newFiles.filetypes.Doc
import newProcessing.buffers.FileBuffer

/**
 * Created by anie on 4/18/2015
 */
trait CSV extends Doc with FileBuffer {

  override def encode(row: NormalRow): String = {

  }

  override def outputSuffix: String = "csv"

  def typesuffix = Vector("csv", "txt")

  def parse: (String) => Vector[String] = CSVHandler.parseline

}
