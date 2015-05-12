package newParser

import files.structure.DataStructure
import newFiles.DataContainer
import newProcessing.Scheduler

/**
 * All operation based class takes in implicit parameter scheduler
 * Parser contains any operation that either matches a sentence
 * or parses a sentence
 */
abstract class Parser(data: DataContainer,
                       struct: DataStructure)(implicit val scheduler: Scheduler)