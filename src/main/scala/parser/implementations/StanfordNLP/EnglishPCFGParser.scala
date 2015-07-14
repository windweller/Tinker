package parser.implementations.StanfordNLP

import edu.stanford.nlp.parser.lexparser.LexicalizedParser
import files.DataContainer
import parser.Parser
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
   * @param generatedColumnName Enter the name that you want the newly generated column to be
   *
   * @return the generated column name (columnName if it's specified)
   */
  override def parse(generatedColumnName: Option[String] = None): String = {
    scheduler.addToGraph(row => scala.concurrent.Future {
      row += (generatedColumnName.getOrElse("parsed") -> lp.parse(struct.getTargetValue(row).get).toString)
    })
    generatedColumnName.getOrElse("Parsed")
  }

}
