package newProcessing

import scala.collection.mutable.ArrayBuffer

/**
 * This stores sequence
 */
abstract class Scheduler(workerCount: Int) extends Operation {

  val opSequence: ArrayBuffer[(Int, Int)] = ArrayBuffer.empty[(Int, Int)]

}
