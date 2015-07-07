package parser.implementations.stanfordNLP

import akka.stream.scaladsl.Flow
import edu.stanford.nlp.parser.lexparser.LexicalizedParser
import files.RowTypes.NormalRow
import parser.Parser
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Aimingnie on 5/11/15
 */
trait EnglishPCFG extends Parser {

  val lp: LexicalizedParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", "-MAX_ITEMS","500000")

  /**
   *
   * @param columnName Enter the name that you want the newly generated column to be
   *
   * @return the generated column name (columnName if it's specified)
   */
  override def parse(columnName: Option[String] = None): String = {
    data.scheduler.graphFlows += Flow[NormalRow].mapAsync[NormalRow](row => scala.concurrent.Future {
      row += (columnName.getOrElse("parsed") -> lp.parse(struct.getTargetValue(row).get).toString)
    })
    columnName.getOrElse("parsed")
  }

}
