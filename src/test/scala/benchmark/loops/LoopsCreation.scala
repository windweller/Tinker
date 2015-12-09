package benchmark.loops

import org.scalameter.api._

/**
 * Created by Aimingnie on 9/23/15
 */
object LoopsCreation extends Bench.LocalTime {

  val sizes: Gen[Int] = Gen.range("size")(300000, 1500000, 300000)

  val ranges: Gen[Range] = for {
    size <- sizes
  } yield 0 until size

  performance of "loop" in {
    measure method "array creation" in {
      using(ranges) in {
        r => val a = Array.fill(0)(r.max)
          a.foreach(i => if (i == r.length) println("done"))
      }
    }
  }

  performance of "loop" in {
    measure method "vector creation" in {
      using(ranges) in {
        r => val v = Vector.fill(0)(r.max)
           v.foreach(i => if (i == r.length) println("done"))
      }
    }
  }

  performance of "loop" in {
    measure method "list creation" in {
      using(ranges) in {
        r => val l = List.fill(0)(r.max)
          l.foreach(i => if (i == r.length) println("done"))
      }
    }
  }

}
