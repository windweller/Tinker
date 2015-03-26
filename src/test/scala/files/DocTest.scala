package files

import files.filetypes.{FileTypes, CSV, Tab}
import files.operations.{FileOp, Sequential}
import files.structure.DataStructure
import files.structure.specifics.BasicNLP
import org.scalatest.FlatSpec
import parser._
import utils.ParameterCallToOption.implicits._

/**
 * Created by anie on 3/21/2015.
 */
class DocTest extends FlatSpec {

  "A Doc" should "get data with csv" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with CSV with Doc
    println(doc.dataIteratorPure.next().length)

//    val structure = new DataStructure() with BasicNLP
  }

  it should "get data with header" in {
    val doc = new DataContainer("E:\\Allen\\NYTFuture\\NYT_results\\nyt_by_para.txt", true) with Tab with Doc
    val it = doc.dataIteratorPure
    var line = 0
    while (it.hasNext) {
      if (it.next().length != 59) {
        println("this has issue: " + line)
      }
      line+=1
    }
  }

  it should "provide data" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with CSV with Doc
    println(doc.data.foreach(e => println(e.mkString("\t"))))
  }

  "A data structure" should "store structure" in {
    val struct = new DataStructure(labelColumnWithName = "this") with BasicNLP
  }

}
