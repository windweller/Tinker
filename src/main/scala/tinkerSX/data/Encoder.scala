package tinkerSX.data

/**
  * Created by Aimingnie on 12/14/15.
  */
trait Encoder {

  //inherited by Output type classes like CSVOutput

  def encode(): Unit

}
