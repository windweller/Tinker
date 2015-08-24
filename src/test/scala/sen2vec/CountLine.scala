package sen2vec

/**
 * Created by anie on 8/21/2015.
 */
object CountLine extends App {
  val of = scala.io.Source.fromFile("E:\\Allen\\R\\emnlp2015\\word2vec\\preSen2vecLexiconTrainingfile.txt")
  val it = of.getLines()
  var c = 0
  while (it.hasNext) {
    it.next()
    c += 1
  }

  println(c)

}
