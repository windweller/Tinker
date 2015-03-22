package files.parsers

import org.scalatest.FlatSpec

/**
 * Created by anie on 3/21/2015.
 */
class CSVHandlerTest extends FlatSpec {

  "parseline()" should "parse a line" in {
    assert(CSVHandler.parseline("1,2,3,4,5,6").mkString("") == "123456")
  }

  "a new object" should "inherit from stackable traits" in {

  }

}
