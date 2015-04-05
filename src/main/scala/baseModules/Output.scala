package baseModules

import java.nio.file.Path

import files.DataContainerTypes._
import files.structure.{DataStructure, DataStructureValue}
import utils.FailureHandle
import utils.ParameterCallToOption.implicits._

import scala.concurrent.Future

/**
 * Created by anie on 4/4/2015
 *
 * This is a high level baseModules module interface
 * all baseModules-related module must inherit this one
 */
trait Output extends FailureHandle {

  def save(it: NormalRow)(implicit file: Option[Path]): Unit = {
    fatal("Cannot use save function without knowing the format of file")
    throw new Exception
  }

  //I don't know if I want this function or not...
  //TODO: Incomplete, need to remove used columns
  def extractDataStructureValue(row: Vector[String], ds: DataStructure): (Vector[String], DataStructureValue) = {
    val dsv = DataStructureValue()
    dsv.idValue = ds.idColumn.map(id => row(id))
    dsv.labelValue = ds.labelColumn.map(label => row(label))
    dsv.attributeValues = ds.attributeColumns.map(indices => indices.foldLeft[Vector[String]](Vector.fill[String](indices.length)(""))
      ( (t,v) => t.updated(v, row(v)) ))

    (row, dsv)
  }

}
