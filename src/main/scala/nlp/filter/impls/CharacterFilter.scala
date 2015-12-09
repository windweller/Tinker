package nlp.filter.impls

import core.DataContainer
import core.structure.DataSelect
import nlp.filter.Filter
import utils.TabPrinter

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Aimingnie on 11/29/15.
  * Warning: in order to make this part work
  * TypedRow needs to have a "deleted" field
  * that indicates if it's filtered out
  *
  * Then other algorithms must avoid deleted field
  */
trait CharacterFilter extends Filter{

  this: DataContainer =>

  var character_limit = 3

  /**
    *
    * @param limit string text must be longer than this limit
    *              to be kept
    */
  def setCharacterLimit(limit: Int): Unit = {

  }

  override def preprocess(newColumn: Option[String], select: DataSelect): Unit = {


  }


}
