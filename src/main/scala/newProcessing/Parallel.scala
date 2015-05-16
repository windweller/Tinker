package newProcessing

import newProcessing.buffers.BufferConfig

/**
 * Created by anie on 4/21/2015.
 */
trait Parallel extends Operation {

  //no need to clean scheduler's opSequence once exec() is done
  def exec(): Unit = {


  }

}
