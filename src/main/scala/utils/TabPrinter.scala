package utils

import java.io.{File, RandomAccessFile}

/**
 * Created by anie on 4/27/2015
 *
 * This is temporary and is never intended as an actual design
 */
case class TabPrinter(loc: String) {

  val f = new File(loc)
  val rafile = new RandomAccessFile(f, "rw")

  def print(row: Seq[Any]): Unit = {
    rafile.seek(f.length())
    rafile.write(row.mkString("\t").concat("\r\n").getBytes)
  }

  def close(): Unit = {
    rafile.close()
  }

}
