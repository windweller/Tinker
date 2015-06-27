package processing


trait Sequential extends Operation {

  //let's hope this works
  def exec(): Unit = {
    val rows = opSequence.pop()
    rows.foreach(row => bufferWrite(row))
    bufferClose()
  }
}
