package core.structure

/**
 * Created by anie on 8/26/2015
 *
 * just provide a blueprint for Doc to work on
 *
 * passed in from DataContainer
 */
case class Schema(stringColumn: Option[Int] = None,
             stringColumnWithName: Option[String] = None,
              stringColumnsWithName: Option[Vector[String]] = None,
              stringColumns: Option[Vector[Int]] = None,
             doubleColumn: Option[Int] = None,
             doubleColumnWithName: Option[String] = None,
             doubleColumnsWithName: Option[Vector[String]] = None,
             doubleColumns: Option[Vector[Int]] = None) {

  lazy val stringColumnNames: Option[Vector[String]] = extractVector(stringColumn, stringColumnWithName, stringColumns, stringColumnsWithName)
  lazy val doubleColumnNames: Option[Vector[String]] = extractVector(doubleColumn, doubleColumnWithName, doubleColumns, doubleColumnsWithName)

  private[this] def extractVector(c1: Option[Int], c2: Option[String], c3: Option[Vector[Int]], c4: Option[Vector[String]]) = {
    if (c1.nonEmpty || c2.nonEmpty)
      getSingleIntStringOption(c1, c2).map(Vector(_))
    else if (c3.nonEmpty || c4.nonEmpty)
      getMultipleIntStringOption(c3, c4)
    else None
  }

  protected def getSingleIntStringOption(a1: Option[Int], a2: Option[String]): Option[String] = {
    if (a1.nonEmpty) a1.map(f => f.toString)
    else a2
  }

  protected def getMultipleIntStringOption(a1: Option[Vector[Int]], a2: Option[Vector[String]]): Option[Vector[String]] = {
    if (a1.nonEmpty) Some(a1.get.map(e => e.toString))
    else a2
  }

}