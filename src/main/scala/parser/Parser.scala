package parser

import java.nio.file.{Files, Path, Paths}

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.filetypes.FileTypes
import files.{DataContainer, Doc}
import processing.OperationType._
import utils.FailureHandle
import processing.Operation
import scala.collection.mutable.ArrayBuffer

/**
 * Parser has a general meaning not just
 * sentence parsing, but tregex matching as well
 *
 * This implements Delayed Execution Pattern
 * we traverse the DataContainer's Iterator only once
 * because all functions are waiting to be executed
 *
 */
abstract class Parser(input: DataContainer with Doc,
                       val rules: Option[Vector[String]] = None) extends FileTypes with FailureHandle with Operation{

  val data: DataContainer with Doc = input
  override val actionStream: ArrayBuffer[(IntermediateResult) => IntermediateResult] = ArrayBuffer.empty[(IntermediateResult) => IntermediateResult]

  override val headerString: Option[Vector[String]] = data.headerString
  override val headerMap: Option[Map[String, Int]] = data.headerMap

  //prepare for tregex
  val parsedRules: Option[Vector[TregexPattern]] = rules match {
    case Some(v) => Some(v.map(e => TregexPattern.compile(e)))
    case None => None
  }

  def this(data: DataContainer with Doc) = this(data, None)
  def this(data: DataContainer with Doc, rules: Vector[String]) = this(data, Some(rules))
}
