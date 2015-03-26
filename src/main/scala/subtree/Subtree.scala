package subtree

import files.DataContainer
import subtree.filetypes.VarroSubtreeXML
import utils.FailureHandle

/**
 * Created by anie on 3/23/2015.
 *
 * Subtree being a trait that's on the same level
 * with Doc (swappable)
 */
trait Subtree extends DataContainer with FailureHandle {

  lazy val data = null

}
