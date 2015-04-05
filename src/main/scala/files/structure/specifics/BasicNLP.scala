package files.structure.specifics

import files.structure.DataStructure
import utils.FailureHandle

/**
 * Created by anie on 3/25/2015.
 *
 * BasicNLP defines one target column with one sentence
 * it can be applied to sentence classification or parsing
 * or matching
 *
 * BasicNLP also works with Mallet format
*/
trait BasicNLP extends FormatChecks with FailureHandle {
  //fail the system is the test is not passed
  override def check(st: DataStructure): Unit = {
    if (st.targetColumn.get.size > 1) fatal("BasicNLP defines target column to be one column with actual sentence")
  }

}
