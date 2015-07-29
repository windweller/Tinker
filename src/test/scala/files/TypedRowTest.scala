package files

import org.scalatest.FlatSpec

/**
 * Created by anie on 7/28/2015.
 */
class TypedRowTest extends FlatSpec {

  behavior of "TypedRow"

  it should "have () method to create" in {
    val row = TypedRow(2)
  }

}
