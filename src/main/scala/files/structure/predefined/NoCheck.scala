package files.structure.predefined

import files.structure.DataStructure

/**
 * Created by Aimingnie on 4/30/15.
 */
trait NoCheck extends DataStructure {

  override def predefinedCheck(): Unit = {}

}
