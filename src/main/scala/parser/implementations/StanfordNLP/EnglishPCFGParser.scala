package parser.implementations.StanfordNLP

import application.Application
import edu.stanford.nlp.parser.lexparser.LexicalizedParser
import files.DataContainer
import files.structure.DataSelect
import parser.Parser
import utils.Timer

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Aimingnie on 5/11/15
 */
trait EnglishPCFGParser extends Parser {

  this: DataContainer =>

  val lp: LexicalizedParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", "-MAX_ITEMS","500000")

  /**
   *
   * This will transfer the parsed Tree class to string representation
   *
   * @param newColumn Enter the name that you want the newly generated column to be
    * @return the generated column name (columnName if it's specified)
   */
  override def parse(newColumn: Option[String] = None, struct: DataSelect): DataContainer with Parser = {
    this.scheduler.addToGraph(row => scala.concurrent.Future {
      if(Application.verbose) Timer.completeOne
      row += (newColumn.getOrElse("parsed") -> lp.parse(struct.getTargetValue(row).get).toString)
    })
    this
  }

}
