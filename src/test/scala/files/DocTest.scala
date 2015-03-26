package files

import files.filetypes.CSV
import files.filetypes.Tab
import files.structure.DataStructure
import files.structure.specifics.BasicNLP
import org.scalatest.FlatSpec
import utils.ParameterCallToOption.implicits._

/**
 * Created by anie on 3/21/2015.
 */
class DocTest extends FlatSpec {

  "A Doc" should "get data with csv" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with Doc with CSV
//    println(doc.dataIteratorPure.next().mkString("\t"))

//    val structure = new DataStructure() with BasicNLP
  }

  it should "get data with header" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testTab.txt", true) with Doc with Tab
    println(doc.dataIteratorPure.next().mkString("\t"))
  }

  it should "provide data" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with Doc with CSV
    println(doc.data.foreach(e => println(e.mkString("\t"))))
  }

  "A data structure" should "store structure" in {
    val struct = new DataStructure(labelColumnWithName = "this") with BasicNLP
  }

}
