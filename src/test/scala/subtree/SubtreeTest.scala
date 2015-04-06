package subtree

import files.DataContainer
import files.filetypes.SVM
import files.structure.specifics.SVMFile
import org.scalatest.FlatSpec
import subtree.filetypes.VarroSubtreeXML

/**
 * Created by anie on 3/23/2015.
 */
class SubtreeTest extends FlatSpec {
  "A Subtree" should "get data with Varro XML" in {

    val doc = new Subtree with SVM with VarroSubtreeXML
    doc.parse(
      ("E:\\Allen\\R\\naacl2015\\subtree\\mTurkAllSentencesTest.xml", "Future")
    )
    doc.generateSentenceFeatures()
    doc.saveSentenceFeatures("E:\\Allen\\R\\naacl2015\\subtree\\TinkerTest.xml")
  }
}
