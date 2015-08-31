package utils

import java.io.PrintWriter
import java.text.NumberFormat

/**
 * Created by anie on 4/26/2015.
 */
object Timer {

  val percentFormat = NumberFormat.getPercentInstance
  percentFormat.setMinimumFractionDigits(5)

  var startTime: Long = System.currentTimeMillis()
  var currentTime: Long = 0

  val taskTotal =  3111328 //NYT: 42833583 // 4017266 // 4252996 // unfiltered tweets: 13145853 //character filtered tweets: 8901153 //after emoticon: 8898482 //final: 8613152 //on country: 3111328
  var currentProgress = 0

  def completeOne(): Unit = {
    currentProgress += 1
    currentTime = System.currentTimeMillis()

    if (currentProgress % 1500 == 0) print()
  }

  def print(): Unit = {
    val writer = new PrintWriter("E:\\Allen\\R\\emnlp2015\\timerTweetCountry.txt", "UTF-8")
    writer.println("Progress: "+currentProgress + " / " + taskTotal + " => " + percentFormat.format(currentProgress/taskTotal))
    val expectedSecs = ((currentTime - startTime) / currentProgress.toDouble) * (taskTotal - currentProgress)

    writer.println("Spent Time: "+ ((currentTime - startTime)/1000) + "s expected time: " + (expectedSecs/1000) + "s")
    writer.close()
  }

}

/**
 * Same as Timer Object but accept a taskNum
 * @param taskNum
 */
class Timer(taskNum: Int) {

  val percentFormat = NumberFormat.getPercentInstance
  percentFormat.setMinimumFractionDigits(5)

  var startTime: Long = System.currentTimeMillis()
  var currentTime: Long = 0

  val taskTotal =  taskNum //NYT: 42833583 // 4017266 // 4252996 // unfiltered tweets: 13145853 //character filtered tweets: 8901153 //after emoticon: 8898482 //final: 8613152 //on country: 3111328
  var currentProgress = 0

  def completeOne(): Unit = {
    currentProgress += 1
    currentTime = System.currentTimeMillis()

    if (currentProgress % 1500 == 0) print()
  }

  def print(): Unit = {
    val writer = new PrintWriter("E:\\Allen\\R\\emnlp2015\\timerTweetCountry.txt", "UTF-8")
    writer.println("Progress: "+currentProgress + " / " + taskTotal + " => " + percentFormat.format(currentProgress/taskTotal))
    val expectedSecs = ((currentTime - startTime) / currentProgress.toDouble) * (taskTotal - currentProgress)

    writer.println("Spent Time: "+ ((currentTime - startTime)/1000) + "s expected time: " + (expectedSecs/1000) + "s")
    writer.close()
  }
}