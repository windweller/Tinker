package parser

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.DataContainer
import parser.implementations.ParserImpl
import parser.implementations.stanford.TregexMatcher
import parser.processing.Processing

import scala.collection.mutable.ArrayBuffer
import scala.util.Success

/**
 * Parser has a general meaning not just
 * sentence parsing, but tregex matching as well
 *
 * Parser()
 */
abstract class Parser(data: DataContainer, rules: Option[Vector[String]] = None)(implicit val system: ActorSystem) extends ParserImpl {

  val iterator = data.dataIterator

  implicit val materializer = ActorFlowMaterializer()

  val actionStream: ArrayBuffer[(DataContainer) => Parser]

  //prepare for tregex
  val parsedRules: Option[Vector[TregexPattern]] = rules match {
    case Some(v) => Some(v.map(e => TregexPattern.compile(e)))
    case None => None
  }

  def this(data: DataContainer, rules: Vector[String])(implicit system: ActorSystem) = this(data, Some(rules))

}
