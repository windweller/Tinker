package files.filetypes.output

import core.TypedRow
import core.structure.DataSelect
import processing.buffers.file.FileOutputFormat

/**
 * Created by anie on 5/18/2015
 */
trait TabOutput extends FileOutputFormat {

  override def encodeHeader(row: TypedRow, select: Option[DataSelect] = None, ignore: Option[DataSelect] = None): Array[String] = {

    val cleanedRow = row.select(select, ignore)

    if (cleanedRow.nonEmpty) {
      Array(cleanedRow.get.keysIterator.mkString("\t"), cleanedRow.get.valuesIterator.mkString("\t"))
    }
    else {
      Array(row.keysIterator.mkString("\t"), row.valuesIterator.mkString("\t"))
    }

  }

  override def encode(row: TypedRow, select: Option[DataSelect] = None, ignore: Option[DataSelect] = None): String = {

    val cleanedRow = row.select(select, ignore)

    if (cleanedRow.nonEmpty) {
      cleanedRow.get.valuesIterator.mkString("\t")
    } else
    row.valuesIterator.mkString("\t")
  }

  override def outputSuffix: String = "tab"

}
