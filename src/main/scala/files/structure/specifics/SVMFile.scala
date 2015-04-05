package files.structure.specifics

import files.structure.DataStructureTypes.Structure
import files.structure.{DataStructureValue, DataStructure}

/**
 * Created by anie on 3/25/2015
 */
trait SVMFile extends FormatChecks{
  //fail the system is the test is not passed
  override def check(st: Structure): Unit = {

    st.left.foreach{st =>
      if (st.idColumn.isEmpty && st.idColumnWithName.isEmpty)
        fatal("Our SVM format still requires an ID")

      if (st.labelColumn.isEmpty && st.labelColumnWithName.isEmpty)
        fatal("A classification label +1/-1, or regression value is still needed")
    }

    st.right.foreach{sv =>
      if (sv.idValue.isEmpty)
        fatal("Our SVM format still requires an ID")

      if (sv.labelValue.isEmpty)
        fatal("Our SVM format still requires a label")

      if (sv.attributeValues.nonEmpty)
        fatal("SVM format does not allow additional attribute values")
    }
  }
}
