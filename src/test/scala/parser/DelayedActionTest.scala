package parser

import org.scalatest.FlatSpec

import scala.collection.mutable.ArrayBuffer

/**
 * Created by anie on 3/28/2015.
 */
class DelayedActionTest extends FlatSpec {
  "delayed action" should "delay action till ready" in {
    val test = new TestA(3)
    val result = test.add(5).add(5)
    println(result.exec())
  }
}

class TestA(val a: Int, newAction: Option[ArrayBuffer[(Int) => Int]]) {
  val action: ArrayBuffer[(Int) => Int] = if (newAction.isEmpty) ArrayBuffer.empty[(Int) => Int] else newAction.get
  def add(b: Int): TestA = {action += (a => a + b); new TestA(a, Some(action))}

  def exec(): Int = {
    var result = a
    action.foreach(r => {result = r.apply(result); })
    result
  }

  def this(a:Int) = this(a, None)
}