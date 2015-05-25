package nlp.preprocess.filters

import newFiles.DataContainer
import newFiles.structure.DataStructure

/**
 * Created by Aimingnie on 4/26/15
 *
 * not sure if this should be in nlp
 * or in fileTypes. And if it should be a
 * module at all
 */
abstract class Filter(val data: DataContainer, val struct: DataStructure) {

  def preprocess(saveLoc: String): Unit

}