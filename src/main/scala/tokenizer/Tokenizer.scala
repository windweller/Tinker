package tokenizer

import files.DataContainer
import files.structure.DataStruct

/**
 * Created by anie on 7/28/2015
 */
trait Tokenizer {

  /**
   * Tokenize is a special type of processing
   * it can't be added to stream flow graph
   * so it has to be handled by iterator
   *
   * @param struct pass in the target
   * @return
   */
  def tokenize(struct: DataStruct): DataContainer with Tokenizer

}
