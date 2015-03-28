package parser

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.DataContainer
import parser.implementations.stanford.TregexMatcher
import parser.processing.Processing

import scala.collection.mutable.ArrayBuffer
import scala.util.Success

/**
 * Parser has a general meaning not just
 * sentence parsing, but tregex matching as well
 *
 * This implements Delayed Execution Pattern
 * we traverse the DataContainer's Iterator only once
 * because all functions are waiting to be executed
 */
class Parser(val data: DataContainer, val outputFile: Option[String] = None,
                       val rules: Option[Vector[String]] = None)(implicit val system: ActorSystem) {

  val actionStream: ArrayBuffer[(DataContainer) => Parser] = ArrayBuffer.empty[(DataContainer) => Parser]

  implicit val materializer = ActorFlowMaterializer()

  //prepare for tregex
  val parsedRules: Option[Vector[TregexPattern]] = rules match {
    case Some(v) => Some(v.map(e => TregexPattern.compile(e)))
    case None => None
  }

  def this(data: DataContainer)(implicit system: ActorSystem) = this(data, None, None)
  def this(data: DataContainer, rules: Vector[String])(implicit system: ActorSystem) = this(data, None, Some(rules))
}
