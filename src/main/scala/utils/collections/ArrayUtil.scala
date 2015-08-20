package utils.collections

import utils.FailureHandle

import scala.collection.GenTraversable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Aimingnie on 4/29/15.
 */
object ArrayUtil extends FailureHandle {

  def arrayPlusArray(arr1: ArrayBuffer[Double], arr2: ArrayBuffer[Double]): ArrayBuffer[Double] = {
    if (arr1.length != arr2.length) fatal("2 arrays don't have the same length")
    ArrayBuffer.iterate(0, arr1.length)(_ + 1).map { i =>
      arr1(i) + arr2(i)
    }
  }

}

