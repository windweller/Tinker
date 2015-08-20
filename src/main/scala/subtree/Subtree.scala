package subtree


import subtree.filetypes.VarroSubtreeXML
import utils.FailureHandle

/**
 * Created by anie on 3/23/2015.
 *
 * Subtree being a trait that's on the same level
 * with Doc (swappable)
 *
 * PMI: p(subtreeA, Future) means the occurances of them appearing together
 * p(subtreeA | Future) means the percentage subtree A appears inside Future
 */
abstract class Subtree() extends FailureHandle {

}
