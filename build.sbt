import sbt._

name := "Tinker"

version := "0.1"

scalaVersion := "2.11.6"

mainClass in (Compile, run) := Some("application.Application")

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.sonatypeRepo("public")

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "IESL Release" at "http://dev-iesl.cs.umass.edu/nexus/content/groups/public",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= {
  val akkaV = "2.3.9"
  Seq(
    //parallel-processing
    "com.typesafe.akka"   %%   "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%   "akka-slf4j"    % akkaV,
    "com.typesafe.akka"   %% "akka-stream-experimental" % "1.0-M4",
    "com.typesafe.akka"   %%   "akka-testkit"  % akkaV   % "test",
    "org.scalatest"       %%   "scalatest"     % "2.2.4" % "test",
    "ch.qos.logback"      % "logback-classic"  % "1.1.2",
    //utilities
    "com.github.nscala-time" %% "nscala-time" % "1.8.0",
    "com.bizo" % "mighty-csv_2.11" % "0.2",
    "com.github.tototoshi" %% "scala-csv" % "1.2.1",
    "org.apache.commons" % "commons-csv" % "1.1",
    "com.chuusai" %% "shapeless" % "2.2.3",
    //Linear Algebra-components
    "org.scalanlp" %% "breeze" % "0.11.2",
    "org.scalanlp" %% "breeze-natives" % "0.11.2",
    "org.scalanlp" %% "breeze-viz" % "0.11.2",
    //NLP-components
    "cc.mallet" % "mallet" % "2.0.7-RC2",
    "edu.emory.clir" % "clearnlp" % "3.2.0",
    "edu.emory.clir" % "clearnlp-dictionary" % "3.1",
    "edu.emory.clir" % "clearnlp-general-en-dep" % "3.2",
    "edu.emory.clir" % "clearnlp-general-en-pos" % "3.2",
    "edu.stanford.nlp" % "stanford-corenlp" % "3.5.1",
    "edu.stanford.nlp" % "stanford-corenlp" % "3.5.1" % "provided" classifier "models",
    //ML-components
    "tw.edu.ntu.csie" % "libsvm" % "3.17",
    //Command-Line arguments
    "com.github.scopt" %% "scopt" % "3.3.0"
  )
}

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheUnzip = false)
assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheOutput = false)
javaOptions in assembly += "-Xmx2g"

initialCommands in console := """
import files.filetypes._
import utils.ParameterCallToOption.Implicits._

println("===============================")
println("Welcome to Tinker 0.12 alpha release")
println("===============================")
"""