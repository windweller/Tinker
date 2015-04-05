package files

import files.DataContainerTypes._
import org.apache.commons.csv.CSVFormat
import utils.FailureHandle
import scala.concurrent.Future

/**
 * Created by anie on 3/19/2015.
 * Doc is the base class
 * it implements all DataContainer methods
 * as they were simple one column text file
 */
trait Doc extends DataContainer with FailureHandle {

  //by doing this, data is only evaluated once
  override lazy val data = if (rawData.isEmpty) process() else rawData.get

  //a fresh iterator every time
  def file = FileIterator(f, header)(typesuffix)

  lazy val headerString: Option[Vector[String]] = file.headerRaw.map(header => super.parse(header))

  //you might want to test this
  lazy val headerMap: Option[Map[String, Int]] = headerString.map(array => Map(array.zip(0 to array.length): _*))

  override def process(): Vector[Vector[String]] = {
    readFileAll[Vector[String]]((line) => super.parse(line))
  }

  protected def readFileAll[T](transform: (String) => T): Vector[T] = {
   file.map(l => transform(l)).toVector
  }

  protected def readFileIterator[T](transform: (String) => T): Iterator[T] = file.map(l => transform(l))


  abstract override def dataIterator: RowIterator = {
    readFileIterator[Either[OrdinalRow, NamedRow]]((line) => {
      if (!header) Left(super.parse(line))
      else Right(super.parseWithHeader(line))
    })
  }

  abstract override def dataIteratorPure: Iterator[Vector[String]] = readFileIterator[Vector[String]]((line) => super.parse(line))

}