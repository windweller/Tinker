package processing


trait Sequential extends Operation {

  //let's hope this works
  //emmm, where's the execution???
  def exec(): Unit = {
    val rows = opSequence.pop()
    rows.foreach(row => bufferWrite(row))
    bufferClose()
  }
}
