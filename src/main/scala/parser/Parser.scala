package parser

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.{DataContainer, Doc}
import files.DataContainerTypes._


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
class Parser(val data: DataContainer with Doc, outputFile: Option[String] = None,
                       val rules: Option[Vector[String]] = None)(implicit val system: ActorSystem) {

  //right now, it can't save or carry out typed result
  val actionStream: ArrayBuffer[(NormalRow) => NormalRow] = ArrayBuffer.empty[(NormalRow) => NormalRow]
  val saveLoc = outputFile.getOrElse(System.getProperty("user.home"))

  protected implicit val materializer = ActorFlowMaterializer()

  //prepare for tregex
  val parsedRules: Option[Vector[TregexPattern]] = rules match {
    case Some(v) => Some(v.map(e => TregexPattern.compile(e)))
    case None => None
  }

  def this(data: DataContainer with Doc)(implicit system: ActorSystem) = this(data, None, None)
  def this(data: DataContainer with Doc, rules: Vector[String])(implicit system: ActorSystem) = this(data, None, Some(rules))
  def this(data: DataContainer with Doc, rules: Option[Vector[String]])(implicit system: ActorSystem) = this(data, None, rules)

}
