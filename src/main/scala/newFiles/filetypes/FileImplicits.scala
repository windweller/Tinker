package newFiles.filetypes

import newFiles.rowTypes._

import scala.collection.parallel.immutable.ParMap
import scala.language.implicitConversions

/**
 * Created by Aimingnie on 4/24/15
 */
trait FileImplicits {

  //this implicitly creates a hashmap
  class EnhancedNormalRow(val r: NormalRow) {

    var hashMappedNormalRow = ParMap.empty[Key, Value]

    /**
     * This implicitly converts ParVec to ParMap
     * Even though it's faster, you still suffer from
     * significant slowdown due to the fact you convert
     * for each row
     * @param key
     * @return
     */
    def get(key: String): Value = {
      if (hashMappedNormalRow.isEmpty)
        hashMappedNormalRow = r.toMap[Key, Value]

      hashMappedNormalRow(Some(key))
    }
  }

  implicit def normalRowToEnhancedRow(r: NormalRow): EnhancedNormalRow = new EnhancedNormalRow(r)

}
