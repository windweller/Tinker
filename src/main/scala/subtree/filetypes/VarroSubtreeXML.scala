package subtree.filetypes

import java.io.{File, FileInputStream, InputStreamReader}
import javax.xml.parsers.SAXParserFactory

import files.filetypes.FileTypes
import org.xml.sax.InputSource
import subtree.filetypes.VarroXMLSAXComponent.VarroXMLSAXHandler

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
  val sentenceVector: Map[String, Vector[String]] = Map.empty[String, Vector[String]]

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

  def saveSentenceFeatures: Unit = {

  }

}
