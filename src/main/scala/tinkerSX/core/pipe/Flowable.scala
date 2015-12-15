package tinkerSX.core.pipe

import tinkerSX.data.Data

/**
  * Created by Aimingnie on 12/9/15.
  *
  */
trait Flowable {

  //require Data
  this: Data =>

  def ~>(): Unit = {

  }

}
