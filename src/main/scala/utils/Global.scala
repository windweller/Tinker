package utils

import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 4/22/2015
 *
 * This is a special Global object
 * that contains information (like generated
 * temporary file's name)
 */
object Global {

  //either ArrayBuffer, or HashMap
  val tempFiles = ArrayBuffer.empty[String]

}
