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
Welcome to Tinker 0.1 alpha release
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

Basic File I/O has a nice high-level abstraction that treats a directory of files and a single file as the same entity, and allow `Iterator` access throught the whole data corpus. The orders of modules are important. General rule of thumb: more specific modules go first (such as `CSV` for `Tab`) before general module (such as `Doc`). They contain information later modules will invoke. Operation modules go last such as `FileOp`.

Advanced File I/O is being developed.

Interface to ML and NLP libraries are being developed.

## Design Structure

The `Operation` module is one of the base modules that faciliate processing. 

## Modules

#### DataContainer

`Doc`: this module deals with files, but mostly provide type-agnostic reading functions.

`CSV`: provide type and parsing information for .csv files.

`Tab`: same above.

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




