package ml.svm

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils

/**
 * Created by anie on 6/28/2015.
 */
object SVM extends App {

  val conf = new SparkConf().setAppName("testSVM").setMaster("local")
  val sc = new SparkContext(conf)

//  val data = MLUtils.loadLibSVMFile(sc, "data/mllib/sample_libsvm_data.txt")

}
