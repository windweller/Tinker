package utils

/**
 * This provides a  centralized support
 * for failure handling
 */
trait FailureHandle {
  protected def fatal(msg: String): Unit = {
    println(msg)
    System.exit(1)
  }

  protected def fail(msg: String): Unit = println(msg)
}
