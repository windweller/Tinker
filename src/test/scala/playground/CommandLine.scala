package playground

/**
 * Created by Aimingnie on 9/27/15
 */
object CommandLine extends App {

  (0 to 100).foreach{e => Thread.sleep(100); print("the time of repeating: " + e + "\r")}

}
