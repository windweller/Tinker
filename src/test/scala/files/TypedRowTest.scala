package files

import core.TypedRow
import core.structure.DataStruct
import org.scalatest.FlatSpec

import scala.collection.mutable
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 7/28/2015
 */
class TypedRowTest extends FlatSpec {

  import TypedRow.Implicits._

  behavior of "TypedRow"

  it should "be able to add different datatypes" in {

    implicit val struct = new DataStruct()
    val row = TypedRow()
    row += "A" -> 0.0
    row += "B" -> 1.0
    row += "C" -> 2.0
    row += "D" -> "this!"
    row += "E" -> 3.0

//    println(struct.featureHeader)
//    println(row.featureVector)
  }

  it should "not add headers when headers exist" in {
    implicit val struct = new DataStruct(
                      sHeader = mutable.HashMap(
                        "A" -> 0,
                        "B" -> 1
                      ))
    val row = TypedRow()

    assert(row.headerExist(Vector("a", "b")), "header would exist")

    row += "A" -> "a"
    row += "B" -> "b"
  }

  it should "not add headers when passing in HashMap when headers exist" in {
    implicit val struct = new DataStruct(
      sHeader = mutable.HashMap(
        "A" -> 0,
        "B" -> 1
      ))
    val row = TypedRow()
    row ++= (mutable.HashMap("A" -> "a", "B" -> "b"), None)
    assert(struct.stringHeader == mutable.HashMap(
      "A" -> 0,
      "B" -> 1
    ), "should stay the same")
  }

  it should "not add headers when passing in Vector Tuple when headers exist" in {
    implicit val struct = new DataStruct(
      sHeader = mutable.HashMap(
        "A" -> 0,
        "B" -> 1
      ))
    val row = TypedRow()
    row ++= ((Vector("A", "B"), Vector("a", "b")), None)
    assert(struct.stringHeader == mutable.HashMap(
      "A" -> 0,
      "B" -> 1
    ), "should stay the same")
  }

  it should "add headers when passing in Vector Tuple" in {
    //the only situation this happens is further processing situation
    implicit val struct = new DataStruct(
      sHeader = mutable.HashMap(
        "A" -> 0,
        "B" -> 1
      ))
    val row = TypedRow(sv = Some(Vector("a", "b")))
    row ++= ((Vector("C", "D"), Vector("c", "d")), None)

    assert(struct.stringHeader == Map("D" -> 3, "A" -> 0, "C" -> 2, "B" -> 1))
  }

}
