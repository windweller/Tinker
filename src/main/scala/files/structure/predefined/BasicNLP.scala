package files.structure.predefined

import files.structure.DataStructure
import utils.FailureHandle

/**
 * Created by Aimingnie on 4/25/15.
 */
trait BasicNLP extends DataStructure with FailureHandle {

  override def predefinedCheck(): Unit = {

    //this already covers most basic NLP tasks
    if (targetColumns.isEmpty &&
      target.isEmpty) fatal("BasicNLP predefined check did not pass: target column cannot be empty.")
  }

}
