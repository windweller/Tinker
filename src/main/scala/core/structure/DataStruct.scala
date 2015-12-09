package core.structure

import core.TypedRow
import core.structure.DataStruct._

import scala.collection.{Iterator, mutable}

/**
 * Created by Aimingnie on 8/31/15
 *
 * Provide a easy way to produce an ordered
 * output
 *
 * No longer be open to users
 *
 * Users should use `DataSelect` or `Schema`
 * to give commands
 */
class DataStruct {

  val headerMap: mutable.HashMap[String, CellInfo] = mutable.HashMap.empty[String, CellInfo]

  //in the end, traverse through ordered header once
  var orderedHeader = Vector.empty[String]


  //this iterates headers orderly
  //and everytime it generates a new iterator
  def headersIterator: Iterator[String] =  {
    orderedHeader.iterator
  }

  //this simply groups header, doesn't add to anything
//  def addToHeaderGroup(headerName: String, headers: Vector[String]): Unit = {
//    headerGroups += (headerName -> headers)
//  }

  //mock a hashmap interface
  def apply(header: String): CellInfo = headerMap(header)

  def foreach[C](f: ((String, CellInfo)) => C): Unit = {
    headerMap.foreach[C](f)
  }

  /**
   * Aux function to add to Vector
   * @param columnName the name to add to hashmap (key)
   * @param rowin corresponding position in the vector of this value, and type info
   */
  def +=(columnName: String, rowin: CellInfo): Unit = {
    if (headerMap.get(columnName).isEmpty) //this additional check might result in inefficiency
      orderedHeader = orderedHeader :+ columnName

    headerMap += (columnName -> rowin)
  }

  def +=(tuple: (String, CellInfo)): Unit = {
    if (headerMap.get(tuple._1).isEmpty)
      orderedHeader = orderedHeader :+ tuple._1

    headerMap += tuple
  }

  def getPosOf(header: String): Option[Int] = headerMap.get(header).map(h => h.pos)

  //this update when header exists, but add new if header doesn't
  def updatePosOf(header: String, pos: Int): Unit = {
        headerMap(header).pos = pos
  }

  //in terms of efficiency, this DOES NOT update the rest of cell's position
  def removeCell(header: String): Option[CellInfo] = headerMap.remove(header)

  def getCellInfoOf(header: String): Option[CellInfo] = headerMap.get(header)

  def getTypeOf(header: String): Option[Symbol] = headerMap.get(header).map(h => h.ty)

  //this gurantees one traversal
//  def ++=(columnName: Vector[String], vectorPos: Range): Unit =
//    addVectorToHeader(headerMap, columnName, vectorPos)
//
//  private[this] def addVectorToHeader(headerA: mutable.HashMap[String, Int],
//                                      columnName: Vector[String], vectorPos: Range): Unit = {
//    vectorPos.indices.foreach { i =>
//      headerA += columnName(i) -> vectorPos(i)
//      orderedHeader = orderedHeader :+ columnName(i)
//    }
//  }

}

object DataStruct {

  val string = 'String
  val double = 'Double
  val int = 'Int

  //instead of String, we use symbol, for fast comparison
  case class CellInfo(var pos: Int, ty: Symbol)

  type Table = Vector[Vector[TypedRow]]
}
