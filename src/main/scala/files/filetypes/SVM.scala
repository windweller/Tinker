package files.filetypes

import java.io.RandomAccessFile
import java.nio.file.Path

import files.structure.DataStructureTypes.Structure

import scala.annotation.tailrec
import scala.language.higherKinds

/**
 * Created by anie on 4/5/2015
 *
 * This is to read in and write out
 * condensed vector:
 * 125:1 130:1
 *
 * Right now it only has write functionality
 */
trait SVM extends FileTypes {

  //right now it only handles DataStructureValue
  //TODO: not very complete
  override def save(row: String)(implicit file: Option[Path],
                                         dt: Option[Structure] = None): Unit = {
    if (file.isEmpty) fatal("Cannot find implicit parameter file")
    val f = file.get.toFile
    val rafile = new RandomAccessFile(f, "rw")
    rafile.seek(f.length())

    dt.foreach(dtv => dtv.right.foreach(e =>
      rafile.write((e.idValue.get + " " + e.labelValue.get + " " + row.concat("\r\n")).getBytes)
    ))

    if (dt.isEmpty) rafile.write((row + "\r\n").getBytes)

    rafile.close()
  }

  //compress to condensed form
  override def compress[T[Int] <: IndexedSeq[Int]]: (T[Int]) => String = (array: T[Int]) =>
    collect(array.iterator, Vector.empty[String], 0).mkString(" ")


  @tailrec
   private[this] def collect(it: Iterator[Int], result: Vector[String], pos: Int): Vector[String] = {
    if (!it.hasNext) result
    else {
      val elem = it.next()
      if (elem != 0) collect(it, result :+ (pos + ":" + elem), pos+1)
      else collect(it, result, pos+1)
    }
  }

}
