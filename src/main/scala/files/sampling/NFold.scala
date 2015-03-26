package files.sampling

/**
 * Created by anie on 3/22/2015.
 */
trait NFold {
  def split(parts: Int): Vector[Array[String]]
}
