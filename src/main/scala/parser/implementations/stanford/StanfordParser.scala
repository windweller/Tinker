package parser.implementations.stanford

import edu.stanford.nlp.parser.lexparser.LexicalizedParser
import files.DataContainerTypes._
import parser.Parser
import processing.OperationType._
import utils.FailureHandle

/**
 * Created by anie on 3/27/2015.
 */
trait StanfordParser extends Parser with FailureHandle {

  val lp: LexicalizedParser

  def parse(rowNum: Option[Int] = None, rowStr: Option[String] = None,
            useGeneratedRow: Boolean, nameForParsedColumn: Option[String] = None): Unit = {
    val columnName = nameForParsedColumn.getOrElse("Parsed")
    if (parsedRules.isEmpty) fatal("Can't proceed if rules aren't specified")
    actionStream += (combinedRow => {
      val result: GeneratedRow = if (useGeneratedRow) {
        if (combinedRow._2.isEmpty) fatal("intermediate result is empty, cannot 'useGeneratedRow'.")
        genResult(combinedRow._2.get, rowNum, rowStr, columnName)
      }
      else genResult(combinedRow._1, rowNum, rowStr, columnName)
      (combinedRow._1, Some(result))
    })
  }

  private[this] def genResult(row: NormalRow, rowNum: Option[Int], rowStr: Option[String], nameForGeneratedColumn: String): GeneratedRow = {
    row.left.map(e => parseWithOrdinal(rowNum, e))
    row.right.map(e => parseWithNamed(rowStr, e, nameForGeneratedColumn))
  }

  private[this] def parseWithOrdinal(rowNum: Option[Int], row: OrdinalRow): OrdinalRow = {
    if (rowNum.isEmpty) fatal("Can't proceed if row number is not specified with ordinal rows")
    parseFunc()(row(rowNum.get))
  }

  private[this] def parseWithNamed(rowStr: Option[String], namedRow: NamedRow, nameForGeneratedColumn: String): NamedRow = {
    if (namedRow.isEmpty) fatal("Can't proceed if row number is not specified with ordinal rows")
    val mappedRulesWithResult = Map(nameForGeneratedColumn -> parseFunc()(namedRow(rowStr.get)).head)
    mappedRulesWithResult
  }

  protected def parseFunc(): String => Vector[String] =
    (sentence: String) => Vector(lp.parse(sentence).toString)


}
