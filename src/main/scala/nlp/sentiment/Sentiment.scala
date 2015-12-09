package nlp.sentiment

import core.structure.DataSelect

/**
  * Created by Aimingnie on 11/29/15.
  */
trait Sentiment {
  def classify(newColumn: Option[String] = None, select: DataSelect): Unit
}
