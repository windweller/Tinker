package searcher

import files.DataContainer
import files.structure.DataSelect

/**
 * Created by aurore on 15/12/15.
 */
trait Searcher {
  def search(newColumn: Option[String] = None,  patterns: Option[List[String]] = None, struct: DataSelect): DataContainer with Searcher
}
