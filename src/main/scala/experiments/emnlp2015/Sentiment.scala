package experiments.emnlp2015

import java.text.{DecimalFormat, NumberFormat}
import java.util.{Collections, Properties}

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import com.github.tototoshi.csv.CSVWriter
import com.typesafe.config.{ConfigFactory, Config}
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.trees.Tree
import newFiles.DataContainer
import newFiles.RowTypes.NormalRow
import newFiles.structure.DataStructure
import org.ejml.simple.SimpleMatrix
import utils.Timer

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

/**
 * Created by anie on 6/7/2015.
 */
class Sentiment(val data: DataContainer, val struct: DataStructure) {

  private val NF: NumberFormat = new DecimalFormat("0.0000")

  //other people's code
  val pipelineProps: Properties = new Properties
  var tokenizerProps: Properties = null

  pipelineProps.setProperty("annotators", "parse, sentiment")
  pipelineProps.setProperty("enforceRequirements", "false")
  tokenizerProps = new Properties
  tokenizerProps.setProperty("annotators", "tokenize, ssplit")

  val tokenizer: StanfordCoreNLP = if (tokenizerProps == null) null else new StanfordCoreNLP(tokenizerProps)
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(pipelineProps)


  def saveSentimentMatching(saveLoc: String): Unit = {

    def extractSentiment(row: NormalRow): Seq[Seq[Any]] = {
      val tweet = row(struct.target.get)
      val annotations: ArrayBuffer[Annotation] = getAnnotations(tokenizer, tweet)

      val result = ArrayBuffer.empty[Seq[String]]

      annotations.foreach(annotation => {
        pipeline.annotate(annotation)

        //Stanford's assumption is there are multiple sentences.
        val coreMapIt = annotation.get(classOf[CoreAnnotations.SentencesAnnotation]).iterator()

        while(coreMapIt.hasNext) {
          val sentence = coreMapIt.next()

          /* output code, we output both score/prob and category for root */
          val tree: Tree = sentence.get(classOf[SentimentCoreAnnotations.AnnotatedTree])
          val rootLabel = sentence.get(classOf[SentimentCoreAnnotations.ClassName])

          //for prob/score, we don't traverse, just output for root
          val copy: Tree = tree.deepCopy
          setIndexLabels(copy, 0)
          val vector: SimpleMatrix = RNNCoreAnnotations.getPredictions(tree)

          val seq = (0 to vector.getNumElements - 1).map(i => NF.format(vector.get(i)))

          //column name, rootLabel, prob score
          result += Seq(struct.getIdValue(row).get, rootLabel) ++ seq
          Timer.completeOne() //add to timer
        }
      })
      result.toSeq
    }

    //====================graph code ===============

    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    val conf: Config = ConfigFactory.load()
    implicit val system = ActorSystem("reactive-tweets", conf)
    implicit val materializer = ActorFlowMaterializer()

    import system.dispatcher

    val tweets: Source[NormalRow, Unit] = Source(() => data.strip)
    val printSink = Sink.foreach[Seq[Seq[Any]]](line => output.writeAll(line))

    val sentimentFlow: Flow[NormalRow, Seq[Seq[Any]], Unit] = Flow[NormalRow].mapAsync[Seq[Seq[Any]]](row => Future {
      extractSentiment(row)
    })

    val g = FlowGraph.closed(printSink) { implicit builder: FlowGraph.Builder =>
      printsink =>

        import FlowGraph.Implicits._

        val bcast = builder.add(Broadcast[NormalRow](15))
        val merge = builder.add(Merge[Seq[Seq[Any]]](15))

        tweets ~> bcast.in

        0 to 9 foreach  { i =>
          bcast.out(i) ~> sentimentFlow ~> merge
        }

        merge ~> printsink
    }.run()



  }

  //eh, not sure if this is correct, it's used for traversal
  private[this] def setIndexLabels (tree: Tree, index: Int): Int = {
    if (tree.isLeaf) {
      return index
    }

    tree.label.setValue(Integer.toString(index))
    var newIndex = index + 1
    for (child <- tree.children) {
      newIndex = setIndexLabels(child, newIndex)
    }

    newIndex
  }

  private[this] def getAnnotations(tokenizer: StanfordCoreNLP, text: String): ArrayBuffer[Annotation] = {
    val annotation: Annotation = new Annotation(text)
    tokenizer.annotate(annotation)
    val annotations = ArrayBuffer.empty[Annotation]
    val coreMapIt = annotation.get(classOf[CoreAnnotations.SentencesAnnotation]).iterator()
    while (coreMapIt.hasNext) {
      val sentence = coreMapIt.next()
      val nextAnnotation: Annotation = new Annotation(sentence.get(classOf[CoreAnnotations.TextAnnotation]))
      nextAnnotation.set(classOf[CoreAnnotations.SentencesAnnotation], Collections.singletonList(sentence))
      annotations += nextAnnotation
    }
    annotations
  }
}
