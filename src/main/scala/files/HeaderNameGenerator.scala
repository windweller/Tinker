package files

/**
 * Created by anie on 3/29/2015.
 */
object HeaderNameGenerator {
  var serial = 0

  def getNext: Int = {serial+=1; serial}
}
