package utils

/**
  * Created by anie on 4/26/2015.
  * Display Spent Time at each completed step.
 */
object Timer {
  var startTime: Long = System.currentTimeMillis()
  var currentTime: Long = 0
  var previousTime: Long = 0

  var currentProgress = 0

  def completeOne(): Unit = {
    currentProgress += 1
    currentTime = System.currentTimeMillis()

    if ((previousTime/10000) != (currentTime/10000)) { //10 seconds approximately
      previousTime = currentTime
      print()
    }
  }

  def print(): Unit = {
    println("Spent Time: "+ ((currentTime - startTime)/1000) + ", " +
      currentProgress + " tasks completed")
  }

}