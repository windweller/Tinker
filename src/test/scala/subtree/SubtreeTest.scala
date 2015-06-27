package subtree

import files.filetypes.svm.SVM
import org.scalatest.FlatSpec
import subtree.filetypes.VarroSubtreeXML

/**
 * Created by anie on 3/23/2015.
 */
class SubtreeTest extends FlatSpec {
  "A Subtree" should "get data with Varro XML" in {

    val doc = new Subtree with SVM with VarroSubtreeXML
    doc.parse(
      ("E:\\Allen\\R\\naacl2015\\subtree\\mTurkAllSentencesFuture.xml", "Future"),
      ("E:\\Allen\\R\\naacl2015\\subtree\\mTurkAllSentencesNANFuture.xml", "NANFuture")
    )
//    doc.generateSentenceFeatures()
//    doc.saveSentenceFeatures("E:\\Allen\\R\\naacl2015\\subtree\\TinkerSentenceFeature.txt")
//    doc.saveSubtreeWithSerialNumber("E:\\Allen\\R\\naacl2015\\subtree\\TinkerSubtreeList.txt")

    doc.generateSentenceSubtrees()
//    doc.saveSentenceSubtrees("E:\\\\Allen\\\\R\\\\naacl2015\\\\subtree\\\\TinkerSentenceSubtree.txt")

  }
}
