package files

import files.filetypes.{FileTypes, CSV, Tab}
import files.operations.{FileOp, Sequential}
import files.structure.DataStructure
import files.structure.specifics.BasicNLP
import org.scalatest.FlatSpec
import parser._
import utils.ParameterCallToOption.implicits._
import utils.OnStartUp._

/**
 * Created by anie on 3/21/2015.
 */
class DocTest extends FlatSpec {

  "A Doc" should "get data with csv" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with CSV with Doc

//    val structure = new DataStructure() with BasicNLP
  }

  it should "get the right file suffixes" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles", true) with CSV with Doc
    doc.data.foreach(e => println(e.mkString("\t")))
  }

  it should "be able to parse when passed as an argument" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with CSV with Doc

    print(doc)
    def print(data: DataContainer): Unit = {
      println(data.dataIteratorPure.next()(0))
    }
  }

  it should "get data with header" in {
    val doc = new DataContainer("E:\\Allen\\NYTFuture\\NYT_results\\nyt_by_para.txt", true) with Tab with Doc
    val it = doc.dataIteratorPure
    var line = 0
    while (it.hasNext) {
      if (it.next().length != 60) {
        println("this has issue: " + line)
        }
      line+=1
    }
  }

  it should "provide data" in {
    val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with CSV with Doc
    doc.data.foreach(e => println(e.mkString("\t")))
  }

  "A data structure" should "store structure" in {
    val struct = new DataStructure(labelColumnWithName = "this") with BasicNLP
  }

}
