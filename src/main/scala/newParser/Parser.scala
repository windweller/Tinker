package newParser

import files.structure.DataStructure
import newFiles.DataContainer
import newProcessing.Scheduler

/**
 * All operation based class takes in implicit parameter scheduler
 * Parser contains any operation that either matches a sentence
 * or parses a sentence
 *
 * It can both add to scheduler or provide a function
 * so user can call and combine/use by themselves
 */
abstract class Parser(data: DataContainer,
                       struct: DataStructure)(implicit val scheduler: Scheduler)