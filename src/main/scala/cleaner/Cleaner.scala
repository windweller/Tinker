package cleaner

import files.DataContainer
import files.structure.DataSelect

/**
 * Created by aurore
 */
trait Cleaner {
  def clean(newColumn: Option[String] = None, struct: DataSelect): DataContainer with Cleaner
}
