package files.operations

/**
 * Created by anie on 3/25/2015.
 */
trait Sequential extends FileOp {
  override def search(piece: String):Unit = {println("test successful!")}
}
