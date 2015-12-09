package nlp.filter

import core.structure.DataSelect

/**
  * Created by Aimingnie on 11/29/15.
  */
trait Filter {

  def preprocess(newColumn: Option[String] = None, select: DataSelect): Unit

}