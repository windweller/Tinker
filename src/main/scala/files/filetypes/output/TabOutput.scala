package files.filetypes.output

import core.RowTypes
import RowTypes._
import core.structure.DataStructure
import processing.buffers.file.FileOutputFormat

/**
 * Created by anie on 5/18/2015
 */
trait TabOutput extends FileOutputFormat {

  override def encodeHeader(row: NormalRow, struct: Option[DataStructure]): Array[String] = {

    if (struct.nonEmpty) {
      val cleanedRow = row.filter(e => !struct.get.ignores.get.contains(e._1))
      Array(cleanedRow.keysIterator.mkString("\t"), cleanedRow.valuesIterator.mkString("\t"))
    }
    else {
      Array(row.keysIterator.mkString("\t"), row.valuesIterator.mkString("\t"))
    }

  }

  override def encode(row: NormalRow, struct: Option[DataStructure]): String = {
    if (struct.nonEmpty) {
      val cleanedRow = row.filter(e => !struct.get.ignores.get.contains(e._1))
      cleanedRow.valuesIterator.mkString("\t")
    } else
    row.valuesIterator.mkString("\t")
  }

  override def outputSuffix: String = "tab"

}
