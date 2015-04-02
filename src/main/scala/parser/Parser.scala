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
 * @param outputFile after the exec() in Parser, it's automatically saved.
 *                   This allows user to specify a desired location, or
 *                   it will be saved under System.getProperty("user.home")
 *
 * @param outputOverride flag this if you want to override your output file
 *                       or otherwise if the file exists, an error is thrown.
 */
abstract class Parser(input: DataContainer with Doc, protected val outputFile: Option[String] = None,
                       protected  val outputOverride: Boolean = false,
                       val rules: Option[Vector[String]] = None) extends FileTypes with FailureHandle with Operation{

  val data: DataContainer with Doc = input
  override val actionStream: ArrayBuffer[(IntermediateResult) => IntermediateResult] = ArrayBuffer.empty[(IntermediateResult) => IntermediateResult]

  override val headerString: Option[Vector[String]] = data.headerString
  override val headerMap: Option[Map[String, Int]] = data.headerMap

  override implicit val saveLoc: Option[Path] = None

  //prepare for tregex
  val parsedRules: Option[Vector[TregexPattern]] = rules match {
    case Some(v) => Some(v.map(e => TregexPattern.compile(e)))
    case None => None
  }

  def this(data: DataContainer with Doc) = this(data, None, false, None)
  def this(data: DataContainer with Doc, rules: Vector[String]) = this(data, None, false, Some(rules))
  def this(data: DataContainer with Doc, outputFile: String, outputOverride: Boolean, rules: Vector[String]) = this(data, Some(outputFile), outputOverride, Some(rules))

}
