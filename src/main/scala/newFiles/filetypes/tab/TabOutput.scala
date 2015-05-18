package newFiles.filetypes.tab

import newFiles.RowTypes._
import newProcessing.buffers.file.FileOutputFormat

/**
 * Created by anie on 5/18/2015.
 */
trait TabOutput extends FileOutputFormat {

  def encodeHeader(row: NormalRow): Array[String] = {
    Array(row.keysIterator.mkString("\t"), row.valuesIterator.mkString("\t"))
  }

  def encode(row: NormalRow): String = {
    row.valuesIterator.mkString("\t")
  }

  def outputSuffix: String = "tab"

}
