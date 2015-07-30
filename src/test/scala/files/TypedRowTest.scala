package files

import org.scalatest.FlatSpec

/**
 * Created by anie on 7/28/2015.
 */
class TypedRowTest extends FlatSpec {

  behavior of "TypedRow"

  it should "have () method to create" in {
    val row = TypedRow(Some(Vector("1","2","3")), Some(Vector(1,2,3)))
    row ++= Vector(1.0,2.0,3.0)
  }

}
