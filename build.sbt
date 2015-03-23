name := "Tinker"

version := "0.1"

scalaVersion := "2.11.5"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "IESL Release" at "http://dev-iesl.cs.umass.edu/nexus/content/groups/public"
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
    "org.apache.commons" % "commons-csv" % "1.1",
    //NLP-components
    "cc.mallet" % "mallet" % "2.0.7-RC2",
    "edu.emory.clir" % "parser/clearnlp" % "3.0.1",
    "edu.emory.clir" % "clearnlp-dictionary" % "3.0",
    "edu.emory.clir" % "clearnlp-general-en-dep" % "3.1",
    "edu.emory.clir" % "clearnlp-general-en-pos" % "3.1",
    "edu.stanford.nlp" % "stanford-corenlp" % "3.5.1"
  )
}

initialCommands in console := """
import files.filetypes._
println("===============================")
println("Welcome to Tinker 0.1 pre-alpha release")
println("===============================")
"""

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")
