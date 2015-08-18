package files

import java.io.File

import org.scalatest.FlatSpec

/**
 * Created by anie on 8/17/2015.
 */
class NFileIteratorTest extends FlatSpec {

  "file filter" should "work" in {
    val preFiles = new File("E:\\Allen\\R\\emnlp2015\\Country_Tweets\\Tweets").listFiles()
    val files = new NFileIterator(preFiles, true)
    while (files.hasNext) {
      files.next()
    }
    println(files.headerString)
  }

}
