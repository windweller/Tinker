package processing

import files.structure.DataStructure


trait Sequential extends Operation {

  def exec(struct: Option[DataStructure] = None): Unit = {
    val rows = opSequence.top
    rows.foreach(row => bufferWrite(row, struct))
    bufferClose()
  }
}
