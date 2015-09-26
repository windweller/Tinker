package benchmark.loops

import org.scalameter.api._
import spire.syntax.cfor._


/**
 * Created by Aimingnie on 9/4/15.
 */
object CforAndForeach extends Bench.LocalTime {

  val sizes: Gen[Int] = Gen.range("size")(300000, 1500000, 300000)

  val ranges: Gen[Range] = for {
    size <- sizes
  } yield 0 until size

  performance of "loop" in {
    measure method "foreach loop" in {
      using(ranges) in {
        r => testNormal(r.max)
      }
    }
  }

  performance of "loop" in {
    measure method "cfor loop" in {
      using(ranges) in {
        r => testCFor(r.max)
      }
    }
  }

  def testNormal(size: Int): Unit = {
    val v = Vector.fill(size)(1)
    v.indices.foreach(i => if (i == v.length) println("done"))
  }

  def testCFor(size: Int): Unit = {
    val v = Vector.fill(size)(1)
    cfor(0)(_ < v.length, _ + 1){i => if (i == v.length) println("done")}
  }

}
