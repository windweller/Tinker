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
abstract class Parser(input: DataContainer,
                       val rules: Option[Vector[String]] = None) extends FailureHandle with Operation{

  val dataContainer: DataContainer = input
  override val actionStream: ArrayBuffer[(IntermediateResult) => IntermediateResult] = ArrayBuffer.empty[(IntermediateResult) => IntermediateResult]

  //prepare for TregexMatcher
  val parsedRules: Option[Vector[TregexPattern]] = rules match {
    case Some(v) => Some(v.map(e => TregexPattern.compile(e)))
    case None => None
  }

  def this(data: DataContainer with Doc) = this(data, None)
  def this(data: DataContainer with Doc, rules: Vector[String]) = this(data, Some(rules))
}
