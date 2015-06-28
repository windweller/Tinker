package files

import java.io.File

import scala.collection.immutable.HashMap

/**
 * Created by Aimingnie on 4/24/15
 * instead of "seamless" like FileIterator,
 * this returns a map of iterators
 *
 * This will replace FileIterator and be the default way
 * to create iterators
 *
 * We still hold the STRONG assumption that every file
 * even in different groups, have the same file structure
 * same # of columns, or headers
 */

object FileMapIterator {

  /**
   * This is the default calling
   * @param folder
   * @param header
   * @param fuzzyMatch turn this on indicating how many letters you want to match for the same file
   *                   For example "Tweets_AK2015_04_17.txt" and "Tweets_AK.txt" are the same file if the
   *                   param is set at 9 (it matches first 9 letters). The ending index is exclusive, and counts
   *                   start from 0.
   * @param suffix     specify the file suffix that will be included
   * @return a HashMap (or Map) of grouped iterators. If fuzzyMatch is set, it returns the matched string as map name
   *         if not, it returns file name (with suffix, unfortunately)
   */
  def getFileMap(folder: String, header: Boolean, fuzzyMatch: Option[Int])(suffix: Vector[String]): Map[String, FileIterator] = {
    val dir = new File(folder)
    if (!dir.isDirectory) {
      //if just a simple file, we strip the suffix
      HashMap(dir.getName.split("\\.")(0) -> new FileIterator(Array(dir), header))
    }
    else {
      val files = dir.listFiles().filter(f => !f.isDirectory).filter(f => suffix.contains(f.getName.split("\\.")(1)))
      val groups = if (fuzzyMatch.nonEmpty) files.groupBy(f => f.getName.substring(0, fuzzyMatch.get)) else files.groupBy(f => f.getName)
      //we then construct a FileIterator for each groups
      groups.map(e => e._1 -> new FileIterator(e._2, header))
    }
  }

}