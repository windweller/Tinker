package tinkerSX.core.pipeline

import tinkerSX.data.Data

/**
  * Created by Aimingnie on 12/9/15.
  *
  */
trait Flowable {
  //require Data to be found
  this: Data =>

  def ~>(): Unit = {

  }

}
