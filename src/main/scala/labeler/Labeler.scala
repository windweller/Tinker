package labeler

import files.DataContainer
import files.structure.DataSelect

/**
  * Class Labeler
  * This implementation creates a new column where all matched rules
  * will be inserted in the sentence as a flag $rule where it was first
  * triggered.
  *
  * @author aurore
  */
trait Labeler {

  /**
    * Trait Labeler
    * @param newColumn name of the new column
    * @param patterns list of patterns with labels
    * @param text name of column where to find text
    * @param tree name of column where to find tree
    * @param place at start, middle or end of triggered words
    * @return
    */
  def label(newColumn:Option[String] = None,
            patterns: Option[List[String]] = None,
            text: DataSelect,
            tree: DataSelect,
            place: Option[String] = Some("start")
           ): DataContainer with Labeler
}
