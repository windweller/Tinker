//package nlp.sentiment.impl
//
//import java.text.{DecimalFormat, NumberFormat}
//import java.util.{Collections, Properties}
//
//import com.github.tototoshi.csv.CSVWriter
//import edu.stanford.nlp.ling.CoreAnnotations
//import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
//import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
//import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
//import edu.stanford.nlp.trees.Tree
//import nlp.sentiment.Sentiment
//import org.ejml.simple.SimpleMatrix
//import utils.Timer
//import scala.collection.mutable.ArrayBuffer
//
///**
// * Created by Aimingnie on 4/25/15
// * It's from SentimentPipeline in StanfordCoreNLP
// *
// * This implementation only takes care of the first sentence
// */
//trait Stanford extends Sentiment{
//
//  private val NF: NumberFormat = new DecimalFormat("0.0000")
//
//
//  def classify(saveLoc: String): Unit = {
//
//    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)
//
//    // We construct two pipelines.  One handles tokenization, if
//    // necessary.  The other takes tokenized sentences and converts
//    // them to sentiment trees.
//    val pipelineProps: Properties = new Properties
//    var tokenizerProps: Properties = null
//
//    pipelineProps.setProperty("annotators", "parse, sentiment")
//    pipelineProps.setProperty("enforceRequirements", "false")
//    tokenizerProps = new Properties
//    tokenizerProps.setProperty("annotators", "tokenize, ssplit")
//
//    val tokenizer: StanfordCoreNLP = if (tokenizerProps == null) null else new StanfordCoreNLP(tokenizerProps)
//    val pipeline: StanfordCoreNLP = new StanfordCoreNLP(pipelineProps)
//
//    /* my code */
//    val it = data.iteratorMap
//    val result = ArrayBuffer.empty[Seq[String]]
//
//    it.foreach { group =>
//      val itr = group._2
//      while (itr.hasNext) {
//        val row = itr.next()
//        val tweet = row(struct.target.get)
//
//        /* stanford code */
//
//        val annotations: ArrayBuffer[Annotation] = getAnnotations(tokenizer, tweet)
//
//        annotations.foreach(annotation => {
//          pipeline.annotate(annotation)
//
//          //Stanford's assumption is there are multiple sentences.
//          val coreMapIt = annotation.get(classOf[CoreAnnotations.SentencesAnnotation]).iterator()
//
//          while(coreMapIt.hasNext) {
//            val sentence = coreMapIt.next()
//
//            /* output code, we output both score/prob and category for root */
//            val tree: Tree = sentence.get(classOf[SentimentCoreAnnotations.AnnotatedTree])
//            val rootLabel = sentence.get(classOf[SentimentCoreAnnotations.ClassName])
//
//            //for prob/score, we don't traverse, just output for root
//            val copy: Tree = tree.deepCopy
//            setIndexLabels(copy, 0)
//            val vector: SimpleMatrix = RNNCoreAnnotations.getPredictions(tree)
//
////            var highestScoreWithLabel: Double = 0
////            (0 to vector.getNumElements - 1).foreach { i =>
////              val score = vector.get(i)
////              if (score > highestScoreWithLabel) highestScoreWithLabel = score
////            }
//
//            val seq = (0 to vector.getNumElements - 1).map(i => NF.format(vector.get(i)))
//
//            //fileName, rootLabel, prob score
//            output.writeRow(Seq(struct.getIdValue(row).getOrElse(group._1), rootLabel) ++ seq)
//            Timer.completeOne() //add to timer
//          }
//
//        })
//
//      }
//    }
//
//  }
//
//  //eh, not sure if this is correct, it's used for traversal
//  private[this] def setIndexLabels (tree: Tree, index: Int): Int = {
//    if (tree.isLeaf) {
//      return index
//    }
//
//    tree.label.setValue(Integer.toString(index))
//    var newIndex = index + 1
//    for (child <- tree.children) {
//      newIndex = setIndexLabels(child, newIndex)
//    }
//
//    newIndex
//  }
//
//  private[this] def getAnnotations(tokenizer: StanfordCoreNLP, text: String): ArrayBuffer[Annotation] = {
//    val annotation: Annotation = new Annotation(text)
//    tokenizer.annotate(annotation)
//    val annotations = ArrayBuffer.empty[Annotation]
//    val coreMapIt = annotation.get(classOf[CoreAnnotations.SentencesAnnotation]).iterator()
//    while (coreMapIt.hasNext) {
//      val sentence = coreMapIt.next()
//      val nextAnnotation: Annotation = new Annotation(sentence.get(classOf[CoreAnnotations.TextAnnotation]))
//      nextAnnotation.set(classOf[CoreAnnotations.SentencesAnnotation], Collections.singletonList(sentence))
//      annotations += nextAnnotation
//    }
//    annotations
//  }
//
//}
