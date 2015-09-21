package core.structure

/**
 * Created by Aimingnie on 4/25/15
 *
 * This could be optimized to do a upfront transformation
 * instead of transforming each time
 *
 * Right now it seems like DataStructure is not for
 * DataContainers, but more for algorithms
 **
 */
trait StructureUtils {

  /*
  *  Util functions
  */

  protected def getSingleIntStringOption(a1: Option[Int], a2: Option[String]): Option[String] = {
    if (a1.nonEmpty) a1.map(f => f.toString)
    else a2
  }

//  protected def getSingleIntStringOptionValue(key: Option[String], row: NormalRow): Option[String] = {
//    key.map(k => row(k))
//  }

  protected def getMultipleIntStringOption(a1: Option[IndexedSeq[Int]], a2: Option[IndexedSeq[String]]): Option[IndexedSeq[String]] = {
    if (a1.nonEmpty) Some(a1.get.map(e => e.toString))
    else a2
  }
//
//  protected def getMultipleIntStringOptionValue(a1: Option[IndexedSeq[Int]], a2: Option[IndexedSeq[String]], row: NormalRow): Option[IndexedSeq[String]] = {
//    if (a1.nonEmpty) Some(a1.get.map(e => row(e.toString)))
//    else if (a2.nonEmpty) Some(a2.get.map(e => row(e)))
//    else None
//  }

}

