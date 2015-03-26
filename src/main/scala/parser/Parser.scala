package parser

import files.DataContainer
import parser.implementations.ParserImpl
import parser.processing.Processing

import scala.collection.mutable.ArrayBuffer

/**
 * Parser has a general meaning not just
 * sentence parsing, but tregex matching as well
 */
abstract class Parser() extends ParserImpl with Processing {

  val actionStream: ArrayBuffer[(DataContainer) => DataContainer] = ArrayBuffer.empty[(DataContainer) => DataContainer]



}
