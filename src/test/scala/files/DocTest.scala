package files

import files.filetypes.CSV
import files.filetypes.Tab
import org.scalatest.FlatSpec

/**
 * Created by anie on 3/21/2015.
 */
class DocTest extends FlatSpec {

  it should "get data with csv" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.txt", true) with Doc with CSV
    println(doc.dataIteratorPure.next().mkString("\t"))
  }

  "A Doc" should "get data with header" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testTab.txt", true) with Doc with Tab
    println(doc.dataIteratorPure.next().mkString("\t"))
  }

}
