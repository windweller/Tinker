package playground

import org.scalatest.FlatSpec
import shapeless.HNil

/**
 * Created by Aimingnie on 7/7/15.
 */
class TypedStackList extends FlatSpec {

  "test" should "work" in {

    val sets = Set(1) :: Set("foo") :: HNil

  }

}
