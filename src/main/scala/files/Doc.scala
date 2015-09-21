package files

import core.structure.DataStruct
import core.{DataContainer, TypedRow}
import utils.OptionToParameter.Implicits._
import utils._

import scala.collection.mutable

/**
 * Extends this means that
 * you are working with an Actual file
 * instead of HTTP, Stream, or any other
 * format.
 *
 * It combines old FileTypes and Doc.
 *
 * This is a downstream of DataContainer,
 * because it implements DataContainer's certain methods and types
 *
 * Doc also automatically
 */
trait Doc extends DataContainer with FailureHandle {

  /**** Abstract methods/variables ****/

  def typesuffix: Vector[String]

  def parse: (String) => Vector[String] //this method is being overriden by differnt types

  /**** Concrete implementations ****/

  //We fill the data struct according to schema
  implicit val ds: DataStruct = new DataStruct()

  //get all files
  def files: Map[String, FileIterator] = FileMapIterator.getFileMap(f, header, fuzzyMatch)(typesuffix)

  //allow peaking, assuming all files share same structure
  lazy private[this] val file = files.head._2

  /**
   * this new method forces HashMap on header/nonHeader structure
   * only inefficient part is the toString, toInt conversion
   *
   * It has two behaviors, both of them greatly impact the whole data representation
   * #1: If header.size > real column.size, it chops off header (because real data is more important)
   * #2: If header.size < real column.size, it will make up the header as Integer index, starting from 0 as usual
   */
  val headerString: Vector[String] =
    if (file.headerRaw.nonEmpty) {
      val vec = parse(file.headerRaw)
      val vec2 = parse(file.firstRow)

      if (vec.size < vec2.size)
        vec ++ (vec.size to vec2.size).map(e => e.toString)
      else vec
    }
    else Vector.iterate("0", parse(file.firstRow).length)(pos => (pos.toInt + 1).toString)

  private[this] def readFileIterator[T](transform: (String) => T)(file: FileIterator): Iterator[T] = file.map(l => transform(l))

  /**
   * For smaller memory footprint, and faster performance
   * Scala mutable HashMap has proven to be the best collection (better than Java)
   * @return a hashmap, key as file name, value as RowIterator
   */
  private[this] def dataIterators: mutable.HashMap[String, Iterator[TypedRow]] = {
    val map = mutable.HashMap.empty[String, Iterator[TypedRow]]
    files.foreach { case (name, fi) =>
      map.put(name, readFileIterator[TypedRow]{ line =>
        specialBuildTypedRow(parse(line))
      }(fi))
    }
    map
  }

  /**
   * This is a highly specialized method that is not generic at all
   * but it guarantees one traversal
   *
   * ignore columns is handled here. Those columns are not even going into
   * type casting or row building
   * @param values
   * @return
   */
  private[this] def specialBuildTypedRow(values: Vector[String]): TypedRow = {
    //to construct TypedRow
    val tr = TypedRow(schema)

    implicit val sm = schema // += requires implicit schema, we only implicitize in here

    //combined with header vector
    values.indices.foreach{ i =>
      if (schema.ifnotIgnore(headerString(i)))
        tr += headerString(i) -> values(i)
    }

    tr
  }

  override def iteratorMap = dataIterators

}
