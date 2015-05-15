package newProcessing

import newProcessing.buffers.BufferConfig

/**
 * Created by anie on 4/21/2015.
 */
trait Parallel extends Operation {

  //clean scheduler's opSequence once exec() is done
  override def exec()(implicit config: Option[BufferConfig]): Unit = {

    opSequence.clear()
  }

}
