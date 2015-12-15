package tinkerSX.data

import tinkerSX.core.pipe.{Flowable, Expression}

/**
  * Created by Aimingnie on 12/9/15.
  */
trait Data extends Flowable {

  implicit val builder = new Expression()

}
