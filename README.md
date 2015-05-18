# Tinker
Tinker is a parallel-by-default File/Directory/Data Management System with additional interface to NLP and ML libraries. Tinker right now uses Scala's awesome Stackable Trait Pattern, and is tightly integrated with powerful Akka Stream to handle task parallelization and pipelining.

## Design Philosophy

We want to make standard large file ML/NLP processing as smooth as easy as possible, even for computers with less power/memory. We parallelize tasks whenever possible, and make most of the tasks asynchronous. Tinker has robust I/O interface that allows users to interact with files (of various format) and databases (PostgreSQL, MySQL...) without pain. It also uses buffering so lower memory computer can still function well with data multiple times larger than its capacity (speed being the drawback).

## Current State

We are undergoing a redesign for the alpha release (finally not "pre-alpha" anymore). New design includes the elimination of module hiearchy (you can inherit them however you want), simplified linearization design, unified type system for `RowIterator`, and a new parallel by default design with Scala Future and Parallel Collection (getting rid of Akka Stream).

We now define higher modules (you can extend them for customization), but when assemble the actual object, only use lower modules with actual implementations.

## Quick Start - Interactive (REPL)

Start by typing `sbt console` from the root of the project. Currently the Unix/Linux Bash script is broken, but the Windows Batch file works, so if you use windows, you can go to `./dist` and use `tinker.bat` file to start.

Then you would see

```
=======================================
Welcome to Tinker 0.2 beta release
=======================================
>
```

Then you can use it like any other Scala REPL from the command line.

## Quick Start - Library

```
import files.filetypes._

  val csv = new DataContainer("././testCSV.csv", header = true) with CSV with Doc
  val tab = new DataContainer("././testTab.tab", header = true) with Tab with Doc
  println(csv.dataIteratorPure.next().mkString("\t"))
```

```
import files._
import parser._
import processing.buffers.FileBuffer
import utils.ParameterCallToOption.implicits._

 val doc = new DataContainer("../testFiles/NYTimes.tab", true) with Tab with Doc
  val parser = new Parser(doc,
    rules = Vector(
      "(VP < (VBG < going) < (S < (VP < TO)))",
      "(VP < (VBG < going) > (PP < TO))",
      "MD < will",
      "MD < ‘ll’"
      )) with Tab with FileBuffer with TregexMatcher with Parallel

  parser.matches(rowStr = "Parse", useGeneratedRow = false)
  parser.exec(outputFile = "E:\\Allen\\NYTFuture\\NYT_sample\\experiment.txt",
    outputOverride = true)
```

Stackable trait pattern means you can swap in and out different modules, like playing Lego. If you are dealing with a tab file, you can choose to use `Tab` module instead of `CSV`.

Basic File I/O has a nice high-level abstraction that treats a directory of files and a single file as the same entity, and allow `Iterator` access throught the whole data corpus. 

Advanced File I/O is being developed.

Interface to ML and NLP libraries are being developed.

## Design

Because we seek to optimize performance (and leverage memory use) to the maximum, we use scheduling as our processing concept. By `import utils.Global.Implicits.scheduler`, you get our default scheduler, that uses `Parallel` and `FileBuffer` module by default, and assumes a 4-core processor (4 workers running concurrently).

All task-related operational modules such as `Parser` requires an implicit scheduler. You can import the global default, or use your own one, and make it implicit such as:

```
implicit val scheduler = new Scheduler(4) with Parallel with FileBuffer
```

This is functionally equivalent to importing the global scheduler. You can create different or multiple schedulers and pass them explicitly to different classes, and this is shown at the advanced technique section.

## Modules

#### DataContainer (f: String, header: Boolean, fuzzyMatch: Int, timerOn: Boolean)

f: the location of the file, can be a directory or a single file

header: whether this corpus has header or not, do note that if selected as true, all files (under the directory) must contain headers and all headers must be the same. If there are more headers than columns, we will fill up headers using ordinal numbers such as `Name | Address | 2 | 3 | 4`

fuzzyMatch: we automatically put files under one directory into groups, and the integer number in fuzzyMatch determines how many characters of the files you want to match. For example: `twitter_AZ_2009_10_4` and `twitter_AZ_2009_10_5` can be put in one group if you set this value at `9`.

timerOn: if you want to set a timer on your task, you must turn this on. However, this will have a very small upfront processing time penalty, because DataContainer will go through your corpus to calculate the total amount of task (row) that will be processed.

`CSV`: provide type and parsing information for .csv files.

`Tab`: same above.

`FileOp`: allow table-like operations (such as union, compression/groupBy) on data. All operations are lazy and are not executed/saved unless explicitly called upon.

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

Here are a list of specialized modules and how to use them.

#### Subtree

```
    val doc = new Subtree with SVM with VarroSubtreeXML
    doc.parse(
      ("../subtree/mTurkAllSentencesFuture.xml", "Future"),
      ("../subtree/mTurkAllSentencesNANFuture.xml", "NANFuture")
    )
    doc.generateSentenceFeatures()
    doc.saveSentenceFeatures("../subtree/TinkerSentenceFeature.txt")
    doc.saveSubtreeWithSerialNumber("../subtree/TinkerSubtreeList.txt")
  }
```

## Minor Issues

1. Table columns are not typed (all represented as string), so we suffer minor performance penalty by converting them on spot.

2. Tinker's column count starts at 0 (not 1)
