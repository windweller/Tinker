package experiments

import java.util.Properties

import edu.stanford.nlp.ling.{CoreAnnotations, CoreLabel}
import edu.stanford.nlp.ling.CoreAnnotations.{TextAnnotation, TokensAnnotation}
import files.DataContainer
import files.filetypes.input.{CSV, Tab}
import files.structure.DataStructure
import nlp.future.FutureRules._
import nlp.preprocess.tokenization.Tokenizer
import nlp.preprocess.tokenization.impl.Stanford
import utils.ParameterCallToOption.Implicits._
import edu.stanford.nlp.pipeline.StanfordCoreNLPServer
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations
import edu.stanford.nlp.simple._
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.pipeline._
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation
import edu.stanford.nlp.util.CoreMap

import scala.collection.JavaConversions._



object Discourse extends App {

//  val sent = new Sentence("Lucy is in the sky with diamonds.")
//  println(sent.incomingDependencyLabels())

  val props = new Properties()
  props.setProperty("annotators", "tokenize,ssplit,pos,depparse")


  val pipeline = new StanfordCoreNLP(props)
  val document = new Annotation("Lucy is in the sky with diamonds.")

  pipeline.annotate(document)


  val sentences = document.get(classOf[CoreAnnotations.SentencesAnnotation])

  for (sentence: CoreMap <- sentences) { // traversing the words in the current sentence
    // this is the Stanford dependency graph of the current sentence
    val dependencies = sentence.get(classOf[EnhancedDependenciesAnnotation])
    println(dependencies)
  }
}
