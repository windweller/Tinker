package subtree.filetypes.VarroXMLSAXComponent

import akka.actor.Status.Success
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

/**
 * This is a simple reimplementation of Java code
 */
class VarroXMLSAXHandler extends DefaultHandler {
  private[this] var negativeCount = false
  private[this] var relativeFactor = 1

  private[this] var tree = ""
  private[this] var rootCount = ""

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
        addresses += pieces(0) + "_" + pieces(1)
        //will this work??
        sentences.getOrElse(pieces(0) + "_" + pieces(1),
          sentences += (pieces(0) + "_" + pieces(1) -> 0))
      }
    }
  }

  override def endElement(uri: String, localName: String, qName: String) {
    if (qName.equalsIgnoreCase("tree")) {
      tree += ")"
      var tempRoot = rootCount.toDouble

      if (negativeCount)
        tempRoot = -tempRoot

      val root: Double = tempRoot / relativeFactor

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