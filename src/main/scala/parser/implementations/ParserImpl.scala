package parser.implementations

import utils.FailureHandle

/**
 * Created by anie on 3/23/2015.
 *
 * this is a template
 */
trait ParserImpl extends FailureHandle{

  //for some it's parse, for some it's match
  def process(data: String): Unit = {
    fatal("must specify a specific parser implementation")
  }

}
