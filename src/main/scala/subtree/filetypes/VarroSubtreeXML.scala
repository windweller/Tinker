package subtree.filetypes

import java.io.{File, FileInputStream, InputStreamReader}
import java.nio.file.Paths
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.InputSource
import subtree.filetypes.VarroXMLSAXComponent.VarroXMLSAXHandler
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import utils.ParameterCallToOption.Implicits._

/**
 * Created by anie on 3/23/2015
 *
 * this serves as general reader
 *
 * This is a weird module that it does not rely on
 * FileIterator to read in files but SAX's own file
 * reading functionality
 */
trait VarroSubtreeXML {

  val typesuffix: Vector[String] = Vector("xml")

  val headerString: Option[Vector[String]] = None
  val headerMap = None

  /* Specific parameters for SAX XML */

  val factory = SAXParserFactory.newInstance()
  val saxParser = factory.newSAXParser()
  val handler = new VarroXMLSAXHandler()

  val sentenceSubtrees: mutable.Map[String, ArrayBuffer[String]] = mutable.Map.empty[String, ArrayBuffer[String]]
  //the string is actually be  MTUrkAllSentences_NANFuture_Varro_767:NANFuture (label later)
  val sentenceVector: mutable.Map[String, ArrayBuffer[Int]] = mutable.Map.empty[String, ArrayBuffer[Int]]

  //fileAddr -> label
  def parse(fileAddrs: (String, String)*): Unit = {
    fileAddrs.foreach { f =>
      val file = new File(f._1)
      val inputStream = new FileInputStream(file)
      val reader = new InputStreamReader(inputStream, "UTF-8")
      val is = new InputSource(reader)
      is.setEncoding("UTF-8")
      handler.label = f._2
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

  //sentenceSubtrees will be filled up
  def generateSentenceSubtrees(): Unit = {
    val it = handler.subtreeSentenceMap.keysIterator
    while (it.hasNext) {
      val key = it.next()
      val valuesIt = handler.subtreeSentenceMap(key).iterator
      while(valuesIt.hasNext) {
        val sentence = valuesIt.next()
        if (sentenceSubtrees.contains(sentence)) {
          val subtrees = sentenceSubtrees(sentence)
          subtrees += key
        }
        else
          sentenceSubtrees += (sentence -> ArrayBuffer.empty[String])
      }
    }
  }

  //TODO: those methods correspond to the old things, need to be re-written

//  def saveSentenceFeatures(loc: String): Unit = {
//    implicit val path = Some(Paths.get(loc))
//    sentenceVector.foreach{e =>
//      val pairs = e._1.split(":")
//      println(e._1)
//      val dsv = new DataStructureValue(idValue = pairs(0), labelValue = pairs(1)) with SVMFile
//      save(compressInt[ArrayBuffer[Int]](e._2))(file = path, struct = Some(Right(dsv)))
//    }
//  }
//
//  def saveSentenceSubtrees(loc: String): Unit = {
//    implicit val path = Some(Paths.get(loc))
//    sentenceSubtrees.foreach {e =>
//      val pairs = e._1.split(":")
//      println(e._1)
//      val dsv = new DataStructureValue(idValue = pairs(0), labelValue = pairs(1)) with SVMFile
//      save(compressString[ArrayBuffer[String]](e._2))(file = path, struct = Some(Right(dsv)))
//    }
//  }
//
//  //this saves subtree as a seperate file
//  def saveSubtreeWithSerialNumber(loc: String): Unit = {
//    implicit val path = Some(Paths.get(loc))
//    var count = 0
//    handler.subtreeList.foreach{e =>
//      val dsv = new DataStructureValue(idValue = count.toString) with NoCheck
//      save(e._1 + "\t" + e._2)(path, Some(Right(dsv)))
//      count += 1
//    }
//  }

  private[this] def unsmoothedfeatureUpdate(a: Int): Int = a + 1
  private[this] def ignoreMagnitudeFeatureUpdate(a: Int): Int = 1

  private[this] def addToSentenceVector(key: String, value: ArrayBuffer[Int]): Unit = {

  }

}
