package parser.processing

import files.DataContainer
import parser.Parser

import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 3/23/2015.
 *
 * This will take/consume a function (match() or process()) that
 * ParseImpl will provide
 *
 * Even though using barebone Future could be easy
 * It does not have the "backpressure" we want, so
 * it might spin into a disaster
 *
 * We'll use Akka Stream implementation here
 * as much as we could
 */
trait Processing extends Parser{

}
