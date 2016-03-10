# Tinker
Tinker is a parallel-by-default File/Multi-File/Data Management System with additional interface to NLP and ML libraries. Tinker right now uses Scala's awesome Stackable Trait Pattern, and is tightly integrated with powerful Akka Stream to handle task parallelization and pipelining.

It offers the ease of reading multiple formated files and merge/process as one, parallelize normal file operations (some of them NLP related) such as filtering, tokenization, compressing/aggregating rows, and so on.

## Primary Usages

1. Convert files from one format to another, similar to Canova  (http://deeplearning4j.org/canova.html#tutorial), but Tinker's performance is much faster (because of default parallel processing), and easier (we don't rely on outside markup language to define data structure).
2. Constituency parsing: with Stanford constituency parser (http://nlp.stanford.edu/software/lex-parser.shtml)
3. Tregex Matching: using a specific regex-like language to search through Stanford constituency tree (http://nlp.stanford.edu/software/tregex.shtml)
4. Future Classification: for psycholinguistics researchers, they may want to check out this paper (http://www.aclweb.org/anthology/N15-2#page=178).

## Design Philosophy

We want to make standard large file ML/NLP processing as smooth and as easy as possible, regardless of the computer's power/memory. We parallelize tasks whenever possible, and make most of the tasks asynchronous. Tinker has robust I/O interface that allows users to interact with files (of various format) and databases (PostgreSQL, MySQL...) without pain. It also uses buffering so lower memory computer can still function well with data multiple times larger than its capacity (speed being the drawback).

Unlike many libaries that rely on configuration files, or 10 config parameters to one method call (or class construction), Tinker uses types (called "modules") to modify and define behavior, which is easier, and can check error at compile time.

## Current State

Tinker only release documentation for stable components, but its nightly built offers more. The next release will include a better API for NLP components (currently experimental and not very well-integrated into Tinker-processing). 

- (next release) 0.15: Added executable generation with `sbt assembly` and new Filters

- 0.14: All core-APIs and a limited number of native modules (Tregex, Parser) are stable and working with new updates.

- 0.13: added testing for parallel module. Further integrate with Akka Flow Graph as well as iterator approach to provide flexible and easy processing integration. Fixed a problem when generating output, `Datastructure` cannot be passed in.

- 0.12: Official alpha-release. Cleaned legacy code (version 0.10 old APIs). Added NLP components on top of Tinker-core. Slight performance increase. Documentation major update.

- 0.11: Preparing for alpha-release, eliminate of module hiearchy (you can inherit them however you want), simplified inheritance/module linearization, unified type system for Tinker-core `RowIterator`, incorporating Akka-Stream 1.0M3 and Akka Stream-graph.

- 0.10: first official pre-alpha release with workable APIs

## For Academics

This library is developed at the time of my undergraduate research project with Dr. Phillip Wolff and Dr. Jinho Choi. Tinker is also included in the CLIR (Computational Linguistics and Information Retrieval) group (a.k.a Emory NLP): https://github.com/clir. The research project focuses on future-orientation detection (a psycholinguistic concept), and deals with files of 60 million lines (NYT corpus) and filtering/processing tweets.  

If you are using this library in research, and if your research is future-orientation related, please cite:

```
Nie, A., Shepard, J., Choi, J., Copley, B., & Wolff, P. (2015, June). Computational Exploration of the Linguistic Structures of Future-Oriented Expression: Classification and Categorization. In NAACL-HLT 2015 Student Research Workshop (SRW) (Vol. 867, No. 164,772, p. 168).
```

## Quick Start - Interactive (REPL)

Start by typing `sbt console` from the root of the project. Currently the Unix/Linux Bash script is broken, but the Windows Batch file works, so if you use windows, you can go to `./dist` and use `tinker.bat` file to start.

Then you would see

```scala
=======================================
Welcome to Tinker 0.15 alpha release
====================================
>
```

Then you can use it like any other Scala REPL from the command line.

## Quick Start - Library

```scala
import files.DataContainer
import files.filetypes.format._
import utils.ParameterCallToOption.implicits._

val workerBook = new DataContainer("./src/test/scala/tutorial/tabFile.tab", header = true) with Tab
val workerBook2 = new DataContainer("./src/test/scala/tutorial/csvFile.csv", header = true) with CSV

workerBook.data.foreach(e => println(e))
workerBook2.data.foreach(e => println(e))
```

Stackable trait pattern means you can swap in and out different modules, like playing Lego. If you are dealing with a tab file, you can choose to use `Tab` module instead of `CSV`. This type of design is consistent throughout Tinker.

Basic File I/O has a nice high-level abstraction that treats a directory of files and a single file as the same entity, and allow `Iterator` access throught the whole data corpus. Every other Tinker function is built on this concept.

## Generating fat JAR (JAR file)

Tinker is now compatible with JAR generation through SBT Assembly (https://github.com/sbt/sbt-assembly) . It provides also a complete command-line interface.
Generation will not automatically add the `stanford-corenlp-models.jar` due to some inconsistency. Please add it on your own to make the executable.

Fully information on the command-line options could be read on launching this command :

```
java -jar Tinker-assembly.jar --help
```

## Processing Design

Because we seek to optimize performance (and leverage memory use) to the maximum, we use scheduling as our processing concept. All the processing has been handled by module `Scheduler`. We generate default scheduler for each `DataContainer`, and you can always configure your own `Scheduler` and pass it into `DataContainer` class.

Our default scheduler uses `Sequential` and `FileBuffer` module.

To switch between parallel processing or sequential processing, put in `core = desired # of processes` when constructing your datacontainer.

All task-related operational modules such as `Parser` requires an implicit scheduler. You can use our default, or create your own one, and make it implicit such as:

```scala
implicit val scheduler = new Scheduler(4) with Parallel with FileBuffer
```

Schedulers are independent for each class, and you should not pass the same scheduler to multiple data containers. If it is passed to multiple containers, it would override the data, causing undesired effect.

`DataContainer` has defined a method `exec()`, which is linked with processing. Whenever `exec()` is called, a file is being saved whether the location is provided or not. If the location is not provided, such file will be a temporary file and deleted when JVM is shut down. Users can use `Global.tempFiles.pop()` to get the full filepath (including name) of the last computed file.

## Modules

#### Implicit Parameter

All the parameters in all classes are defined as option parameters. It is a Scala-unique concept that may not require your understanding. Add `import utils.ParameterCallToOption.Implicits._` at the beginning of your file to use. If you are using the commandline interface, this is already imported for your convenience.

#### DataContainer

```scala
class DataContainer(val f: Option[String] = None,
                              val header: Boolean = true,
                              val fuzzyMatch: Option[Int] = None,
                              val rTaskSize: Option[Int] = None)
                              (implicit val pscheduler: Option[Scheduler] = None)
```

`f: String`: the location of the file, can be a directory or a single file

`header: Boolean`: whether this corpus has header or not, do note that if selected as true, all files (under the directory) must contain headers and all headers must be the same. If there are more headers than columns, we will fill up headers using ordinal numbers such as `Name | Address | 2 | 3 | 4`

`fuzzyMatch: Int`: we automatically put files under one directory into groups, and the integer number in fuzzyMatch determines how many characters of the files you want to match. For example: `twitter_AZ_2009_10_4` and `twitter_AZ_2009_10_5` can be put in one group if you set this value at `9`.

`rTaskSize: Int`: 

--Extension Modules--

`CSV`: provide type and parsing information for .csv files.

`Tab`: same above.

`FileOp`: allow table-like operations (such as union, compression/groupBy) on data. All operations are lazy and are not executed/saved unless explicitly called upon.

Examples Usage:

1.Use Sequential processing to convert Tab-delimited file to CSV file.

```scala
  "sequential processing" can "transform tab file into csv format" in {

    val scheduler = new Scheduler with FileBuffer with Sequential with CSVOutput
    val data = new DataContainer("./src/test/scala/tutorial/data/tabFile.tab", header = true)(scheduler) with Tab

    data.save("./src/test/scala/tutorial/data/generatedCSV.csv")
  }
```

2.The same task can be done through parallel processing: using 4 threads to convert CSV file to a Tab-delimited file.
```scala
//other imports are omitted
import utils.ParameterCallToOption.Implicits._

class TinkerParallel extends TestKit(ActorSystem("testsystem"))
                          with FlatSpecLike with BeforeAndAfterAll {

  "parallel processing" can "transform csv file into tab format" in {
    val scheduler = new Scheduler(4) with FileBuffer with Parallel with TabOutput
    val data = new DataContainer("./src/test/scala/tutorial/data/csvFile.csv", header = true)(scheduler) with CSV

    data.save("./src/test/scala/tutorial/data/generatedTab.tab")
  }

}
```

#### Parser

`TregexMatcher`: an API to Stanford Tregex library. The parsed string has to be a form recognizable from Stanford parser.

`FileBuffer`: This is a module you call when you are attaching an operational module to your algorithm or data container. This module is mandatory for any processing, becasuse of Tinker's delayed execution pattern. In the future, other modules such as DatabaseBuffer could be developed to substitute this.

## Base Modules

You will only inherit base modules if you wish to customize and develop your own modules. Base modules at most occasions don't provide concrete implementations. You need to manually override those functions.

`FileTypes`: This defines basic functions that are associated with files, including protocols for reading/writing.

`Output`: specify an even higher level output abstraction than `FileTypes`, include basically protocols for writing.

`Buffer`: used mostly by algorithms to determine what the buffer implementation should be - save the intermediate computation and later retrieve them.s

`Operation`: inherits `Buffer`. Inherits this class to add Tinker's processing framework to any base class.


## Advanced Usage

#### Custom Module

Here is a way to write your custom module, and build into the pipeline. Imagine you are not satisfied with the default feature extractor in the Tinker. You want to build a custom `SpeacialFeatureExtractor`. What you can do is this (by using Scala's self-type):

```scala
  trait SpeacialFeatureExtractor {
    this: DataContainer =>
    
      def addSpecialFeatures(): Unit = {
        //after some computations
        //add to the Akka Stream Graph
        this.scheduler.addToGraph(row => scala.concurrent.Future{
           row
        })
        //or add to the iterator pipeline
        val it = getIteratorFunction()
        scheduler.opSequence.push(it)
      }
  }
  
```

If you want to invoke other dependencies, so for example, if this `specialFeatureExtractor` depends on constituency parsing result:

```scala
  trait SpeacialFeatureExtractor {
    this: DataContainer with AbstractConstituencyParser =>
    
      def addSpecialFeatures(): Unit = {
        //after some computations
        //add to the Akka Stream Graph
        this.scheduler.addToGraph(row => scala.concurrent.Future{
           row
        })
        //or add to the iterator pipeline
        val it = getIteratorFunction()
        scheduler.opSequence.push(it)
      }
  }
```

Every abstract modules will provide information on how to access their features from the `NormalRow`.

## Minor Issues

1. Table columns are not typed (all represented as string - for faster HashMap performance instead of Shapeless' HList https://github.com/milessabin/shapeless), so we suffer minor performance penalty by converting them on spot.

2. Tinker's column count starts at 0 (not 1)

3. Operation module should be a subsidiary module of DataContainer. They are seperate right now due to historical reasons and cleaner code base. They might be combined in the future.
