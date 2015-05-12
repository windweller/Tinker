package newProcessing

import newProcessing.buffers.BufferConfig

/**
 * Created by anie on 4/21/2015.
 */
trait Parallel extends Operation {

  override def exec()(implicit config: Option[BufferConfig]): Unit = {

  }

}
