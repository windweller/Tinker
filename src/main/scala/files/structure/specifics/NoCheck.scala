package files.structure.specifics

import files.structure.DataStructureTypes._

/**
 * Created by anie on 4/5/2015.
 */
trait NoCheck extends FormatChecks {
  override def check(st: Structure): Unit = {}
}
