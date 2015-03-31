package parser

import java.nio.file.{Files, Path, Paths}

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import edu.stanford.nlp.trees.tregex.TregexPattern
import files.filetypes.FileTypes
import files.{DataContainer, Doc}
import files.DataContainerTypes._
import utils.FailureHandle


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
                       outputOverride: Boolean = false,
                       val rules: Option[Vector[String]] = None)(implicit val system: ActorSystem) extends FileTypes with FailureHandle{

  override val headerString: Option[Vector[String]] = data.headerString
  override val headerMap: Option[Map[String, Int]] = data.headerMap

  //right now, it can't save or carry out typed result
  val actionStream: ArrayBuffer[(NormalRow) => NormalRow] = ArrayBuffer.empty[(NormalRow) => NormalRow]
  implicit val saveLoc: Path = outputFile match {
    case Some(file) =>
      val permFile = Paths.get(file)
      if (Files.isDirectory(permFile)) fatal("outputfile cannot be a directory")
      if (Files.exists(permFile) && !outputOverride) fatal("output file already exist")
      permFile
    case None => Paths.get(System.getProperty("user.home")).resolve(".Tinker").resolve("parserTmp."+typesuffix.head)
  }

  protected implicit val materializer = ActorFlowMaterializer()

  //prepare for tregex
  val parsedRules: Option[Vector[TregexPattern]] = rules match {
    case Some(v) => Some(v.map(e => TregexPattern.compile(e)))
    case None => None
  }

  def this(data: DataContainer with Doc)(implicit system: ActorSystem) = this(data, None, false, None)
  def this(data: DataContainer with Doc, rules: Vector[String])(implicit system: ActorSystem) = this(data, None, false, Some(rules))
//  def this(data: DataContainer with Doc, rules: Option[Vector[String]])(implicit system: ActorSystem) = this(data, None, false, rules)


}
