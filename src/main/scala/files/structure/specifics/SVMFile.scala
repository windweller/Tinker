package files.structure.specifics

import files.structure.DataStructure

/**
 * Created by anie on 3/25/2015.
 */
trait SVMFile extends FormatChecks{
  //fail the system is the test is not passed
  override def check(st: DataStructure): Unit = {
    if (st.idColumn.isEmpty && st.idColumnWithName.isEmpty)
      fatal("Our SVM format still requires an ID")

    if (st.labelColumn.isEmpty && st.labelColumnWithName.isEmpty)
      fatal("A classification label +1/-1, or regression value is still needed")
  }

}
