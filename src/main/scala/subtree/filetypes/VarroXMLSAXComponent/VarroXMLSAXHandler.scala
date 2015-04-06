package subtree.filetypes.VarroXMLSAXComponent

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * This is a simple reimplementation of Java code
 *
 */
class VarroXMLSAXHandler extends DefaultHandler {

  private[this] var negativeCount = false //exclusively used if we want to have initial parameter value

  private[this] var tree = ""
  private[this] var rootCount = ""

  //this will add label to id
  //format: MTUrkAllSentences_163:Future
  var label = ""

  //Double-> either PMI or relative value
  val subtreeList = mutable.HashMap.empty[String, Double]
  val subtreeSentenceMap = mutable.HashMap.empty[String, ArrayBuffer[String]]
  val sentences = mutable.HashMap.empty[String ,Integer]

  var inTree = false
  var inAddr = false

  var addresses = ArrayBuffer.empty[String]

  override def startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
    if (qName.equalsIgnoreCase("subtree")) {
      rootCount = attributes.getValue("rootCount")
    }

    if (qName.equalsIgnoreCase("tree")) {
      inTree = true
    }

    if (qName.equalsIgnoreCase("addresses")) {
      inAddr = true
    }

    if (qName.equalsIgnoreCase("node")) {
      //2 kinds of nodes: subtree node, and address node,
      //differentiable by attributes
      if (attributes.getValue("label") != null && inTree) {
        tree += "(" + attributes.getValue("label") + " "
      }
      else if (attributes.getValue("id") != null && inAddr) {
        val pieces = attributes.getValue("id").split(":")
        addresses += pieces(0) + "_" + pieces(1)+ ":" + label
        //will this work??
        sentences.getOrElse(pieces(0) + "_" + pieces(1)+ ":" + label,
          sentences += (pieces(0) + "_" + pieces(1) + ":" + label -> 0))
      }
    }
  }

  override def endElement(uri: String, localName: String, qName: String) {
    if (qName.equalsIgnoreCase("tree")) {
      tree += ")"
      var tempRoot = rootCount.toDouble

      if (negativeCount)
        tempRoot = -tempRoot

      val root: Double = tempRoot

      //if we already have the tree inside
//      subtreeList.computeIfPresent(tree, (string, val) => val + root)
//      subtreeList.putIfAbsent(tree, root)
      subtreeList.get(tree) match {
        case Some(score) =>
          subtreeList.update(tree, score + root)
        case None =>
          subtreeList += (tree -> root)
      }
      inTree = false
    }

    if (qName.equalsIgnoreCase("addresses")) {
      inAddr = false
    }

    //close parentheses for tree
    if (qName.equalsIgnoreCase("node")) {
      if (inTree)
        tree += ")"
    }

    //save tree, rootCount to hashmap
    if (qName.equalsIgnoreCase("subtree")) {
      subtreeSentenceMap.put(tree, addresses)
      tree = ""
      addresses = ArrayBuffer.empty[String]

    }
  }

}
