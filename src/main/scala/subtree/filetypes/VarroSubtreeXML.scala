package subtree.filetypes

import java.io.{File, FileInputStream, InputStreamReader}
import javax.xml.parsers.SAXParserFactory

import files.filetypes.FileTypes
import org.xml.sax.InputSource
import subtree.filetypes.VarroXMLSAXComponent.VarroXMLSAXHandler
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 3/23/2015
 *
 * this serves as general reader
 *
 * This is a weird module that it does not rely on
 * FileIterator to read in files but SAX's own file
 * reading functionality
 */
trait VarroSubtreeXML extends FileTypes {

  override val typesuffix: Vector[String] = Vector("xml")

  val headerString: Option[Vector[String]] = None
  val headerMap = None

  /* Specific parameters for SAX XML */

  val factory = SAXParserFactory.newInstance()
  val saxParser = factory.newSAXParser()
  val handler = new VarroXMLSAXHandler()

  val ldaSentences: Map[String, Vector[String]] = Map.empty[String, Vector[String]]
  val sentenceVector: mutable.Map[String, ArrayBuffer[Int]] = mutable.Map.empty[String, ArrayBuffer[Int]]

  def parse(fileAddrs: String*): Unit = {
    fileAddrs.foreach { f =>
      val file = new File(f)
      val inputStream = new FileInputStream(file)
      val reader = new InputStreamReader(inputStream, "UTF-8")
      val is = new InputSource(reader)
      is.setEncoding("UTF-8")
      saxParser.parse(is, handler)
    }
  }

  //since this is for all, we don't need to worry about
  //any matching sentence problem
  def generateSentenceFeatures(): Unit = {
    var counter = 0

    val it = handler.subtreeSentenceMap.keysIterator
    while(it.hasNext) {
      val key = it.next()
      val valuesIt = handler.subtreeSentenceMap(key).iterator

      while(valuesIt.hasNext) {
        val sentence = valuesIt.next()
        if (sentenceVector.contains(sentence)) {
          val features = sentenceVector(sentence)
          //change here for update method
          features.update(counter, unsmoothedfeatureUpdate(features(counter)))
        }
        else { //create an arraybuffer with equal size
          val temp = ArrayBuffer.fill(handler.subtreeList.size)(0)
          temp.update(counter, 1)
          sentenceVector += (sentence -> temp)
        }
      }
      counter += 1
    }
  }

  def saveSentenceFeatures(): Unit = {
    
  }

  private[this] def unsmoothedfeatureUpdate(a: Int): Int = a + 1
  private[this] def ignoreMagnitudeFeatureUpdate(a: Int): Int = 1

  private[this] def addToSentenceVector(key: String, value: ArrayBuffer[Int]): Unit = {

  }

}
