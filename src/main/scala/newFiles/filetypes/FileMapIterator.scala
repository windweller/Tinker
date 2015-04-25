package newFiles.filetypes

import java.io.File

/**
 * Created by Aimingnie on 4/24/15
 * instead of "seamless" like FileIterator,
 * this returns a map of iterators
 *
 * This will replace FileIterator and be the default way
 * to create iterators
 */

object FileMapIterator {

  def getFileMap(folder: String, header: Boolean)(suffix: Vector[String]): Map[String, FileIterator] = {
    val dir = new File(folder)
//    val files = if (dir.isDirectory) dir.listFiles().filter(f => !f.isDirectory).filter(f => suffix.contains(f.getName.split("\\.")(1)))
//    else Array(dir)
//    if (dir.isDirectory) new FileIterator(files, header) else new FileIterator(Array(dir), header)

    if (!dir.isDirectory) {

    }

  }

}