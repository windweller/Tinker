package parser

import java.nio.file.{Files, Path, Paths}

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.filetypes.FileTypes
import files.{DataContainer, Doc}
import files.DataContainerTypes._
import utils.FailureHandle

import ParserType._
import scala.collection.mutable.ArrayBuffer

/**
 * Parser has a general meaning not just
 * sentence parsing, but tregex matching as well
 *
 * This implements Delayed Execution Pattern
 * we traverse the DataContainer's Iterator only once
 * because all functions are waiting to be executed
 *
 * @param outputFile after the exec() in Parser, it's automatically saved.
 *                   This allows user to specify a desired location, or
 *                   it will be saved under System.getProperty("user.home")
 */
abstract class Parser(val data: DataContainer with Doc, protected val outputFile: Option[String] = None,
                       protected  val outputOverride: Boolean = false,
                       val rules: Option[Vector[String]] = None)(implicit val system: ActorSystem) extends FileTypes with FailureHandle{

  override val headerString: Option[Vector[String]] = data.headerString
  override val headerMap: Option[Map[String, Int]] = data.headerMap

  //right now, it can't save or carry out typed result
  val actionStream: ArrayBuffer[(NormalRow, Option[GeneratedRow]) => (NormalRow, Option[GeneratedRow])] =
                              ArrayBuffer.empty[(NormalRow, Option[GeneratedRow]) => (NormalRow, Option[GeneratedRow])]

  implicit val saveLoc: Option[Path] = None

  protected implicit val materializer = ActorFlowMaterializer()

  //prepare for tregex
  val parsedRules: Option[Vector[TregexPattern]] = rules match {
    case Some(v) => Some(v.map(e => TregexPattern.compile(e)))
    case None => None
  }

  def this(data: DataContainer with Doc)(implicit system: ActorSystem) = this(data, None, false, None)
  def this(data: DataContainer with Doc, rules: Vector[String])(implicit system: ActorSystem) = this(data, None, false, Some(rules))
  def this(data: DataContainer with Doc, outputFile: String, outputOverride: Boolean, rules: Vector[String])(implicit system: ActorSystem) = this(data, Some(outputFile), outputOverride, Some(rules))

}

object ParserType extends FailureHandle {
  //first one is original, second one is the processed
  type GeneratedRow = NormalRow
  type IntermediateResult = (NormalRow, GeneratedRow)

  def combine(a: NormalRow, b: GeneratedRow): NormalRow = {
    if (a.isLeft && b.isLeft)
      a.left.flatMap(a => b.left.map(b => a ++ b))
    else if (a.isRight && b.isRight)
      a.right.flatMap(a => b.right.map(b => a ++ b))
    else {fatal("Row must be either array based or mapped row based."); throw new Exception}
  }
}
