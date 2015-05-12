package newParser

import newProcessing.{Scheduler, Operation}

/**
 * All operation based class takes in implicit parameter scheduler
 */
abstract class Parser()(implicit val scheduler: Scheduler) {


}