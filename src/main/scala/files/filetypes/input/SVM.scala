package files.filetypes.input

/**
 * This is tricky class because SVM
 * is not a real format, but we
 * need a way to mark dense vector vs. sparse vector
 */
trait SVM {

  //right now it only handles DataStructureValue
  //TODO: not very complete (Not handling DataStructure, just DataStructureValue)
//  override def save(row: String)(implicit file: Option[Path],
//                                 dt: Option[Structure] = None): Unit = {
//    if (file.isEmpty) fatal("Cannot find implicit parameter file")
//
//    val f = file.get.toFile
//    val rafile = new RandomAccessFile(f, "rw")
//    rafile.seek(f.length())
//
//    dt.foreach(dtv => {
//      dtv.right.foreach {e =>
//        val prefix = if (e.idValue.nonEmpty && e.labelValue.nonEmpty) e.idValue.get + "\t" + e.labelValue.get + "\t"
//        else if (e.idValue.nonEmpty) e.idValue.get + "\t"
//        else if (e.labelValue.nonEmpty) e.labelValue.get + "\t"
//        else ""
//        rafile.write((prefix + row.concat("\r\n")).getBytes)
//      }
//      dtv.left.foreach { e =>
//
//      }
//    }
//    )
//
//    if (dt.isEmpty) rafile.write((row + "\r\n").getBytes)
//
//    rafile.close()
//  }
//
//  //compress to SVM's condensed form
//  override def compressInt[T <: IndexedSeq[Int]]: (T) => String = (array: T) =>
//    collect(array.iterator, Vector.empty[String], 0).mkString("\t")
//
//  override def compressString[T <: IndexedSeq[String]]: (T) => String = (array: T) => array.mkString("\t")
//
//
//  @tailrec
//  private[this] def collect(it: Iterator[Int], result: Vector[String], pos: Int): Vector[String] = {
//    if (!it.hasNext) result
//    else {
//      val elem = it.next()
//      if (elem != 0) collect(it, result :+ (pos + ":" + elem), pos+1)
//      else collect(it, result, pos+1)
//    }
//  }

}
