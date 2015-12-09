package benchmark.loops

import org.scalameter.api._

/**
 * Created by Aimingnie on 9/23/15.
 */
object Loops extends Bench.LocalTime {

  val sizes: Gen[Int] = Gen.range("size")(300000, 1500000, 300000)

  val arrays: Gen[Array[Int]] = for {
    size <- sizes
  } yield Array.fill(size)(0)

  val vectors: Gen[Vector[Int]] = for {
    size <- sizes
  } yield Vector.fill(size)(0)

  val lists: Gen[List[Int]] =  for {
    size <- sizes
  } yield List.fill(size)(0)

  performance of "loop" in {
    measure method "array" in {
     using(arrays) in {
       r => r.foreach(i => if (i == r.length) println("done"))
     }
    }
  }

//  performance of "loop" in {
//    measure method "list" in {
//      using(lists) in {
//        r => r.foreach(i => if (i == r.length) println("done"))
//      }
//    }
//  }

  performance of "loop" in {
    measure method "vector" in {
      using(vectors) in {
        r => r.foreach(i => if (i == r.length) println("done"))
      }
    }
  }



}
