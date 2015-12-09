package nlp.future

import scala.collection.mutable.ListBuffer
import core.fun.Applicative

/**
 * Created by Aimingnie on 9/21/15.
 */
object FutureUtils {

  /**
   * load future rules externally
   * @param fileloc
   * @param tndoc
   * @param tcdoc
   * @return
   */
  def loadFutureRules(fileloc: String, tndoc: Option[String] = None, tcdoc: Option[String] = None): List[String] = {
    val patterns = scala.io.Source.fromFile(fileloc).getLines().toList
    val tnterms = tndoc.map(doc => scala.io.Source.fromFile(doc).getLines().toList)
    val tcterms = tcdoc.map(doc => scala.io.Source.fromFile(doc).getLines().toList)

    val result = ListBuffer.empty[String]
    patterns.foreach { p =>
      if (p.contains("TN|TC") || p.contains("TC|TN")) {
        //just replace all TN and TC
        implicitly[Applicative[Option]].map2(tnterms, tcterms){ (tn, tc) =>
          result ++= tn.map(n => p.replace("TN|TC", n))
          result ++= tc.map(c => p.replace("TN|TC", c) )
        }
      }
      else if (p.contains("TN")) {
        tnterms.foreach(tn => result ++= tn.map(n => p.replace("TN|TC", n)))
      }
      else if (p.contains("TC")) {
        result ++= tcterms.get.map(tc => p.replace("TC", tc))
      }
      else result += p
    }

    if (result.nonEmpty) result.toList
    else patterns
  }

}
