package files

import com.bizo.mighty.csv.CSVWriter
import utils.FailureHandle
import scala.collection.mutable.ArrayBuffer
import scala.language.higherKinds

/**
 * Created by anie on 3/19/2015.
 * T: is the data output type you want
 */
trait Doc extends DataContainer with FailureHandle {

  def save(loc: String): Unit
  def parse: (String) => Seq[String]

  protected def saveFile(data: ArrayBuffer[Array[String]], outputFile: String): Unit = {
    val output: CSVWriter = CSVWriter(outputFile)
    data.foreach(d => output.write(d))
  }

  //uses high-performance FileIterator
  protected def readFile[T[String]](loc: String, header: Boolean, transform: (String) => T[String]): ArrayBuffer[T[String]] = {

    val result = ArrayBuffer[T[String]]()
    val file = FileIterator(loc, header)

    while (file.hasNext) {
      result += transform(file.next())
    }

    result
  }
}
