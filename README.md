# Tinker
Tinker is a parallel-by-default File/Directory Management System with additional interface to NLP and ML libraries. Tinker right now uses Scala's awesome Stackable Trait Pattern, and just finished it's asynchronous File I/O implementation.

## Design Philosophy

We want to make standard large file ML/NLP processing as smooth as easy as possible, even for computers with less power/memory. We only keep operations within one module coherent. Operations between modules or involves several modules will be provided with files saving/reading as buffer. If no explicit location specified, Tinker will create temporary files that will be deleted after JVM exits.

## Quick Start - Interactive (REPL)

Start by typing `sbt console` from the root of the project. Currently the Unix/Linux Bash script is broken, but the Windows Batch file works, so if you use windows, you can go to `./dist` and use `tinker.bat` file to start.

Then you would see

```
=======================================
Welcome to Tinker 0.1 pre-alpha release
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

## Modules

#### Global

(those modules don't belong to a specific base class, yet most of them are associated with processing)

`Parallel`: This module guarantees the processing done in parallel. It is one of the more universal modules that should be included whenever you are adding an action-related module to your base class.

`Sequential`: similar to `Parallel` but not yet implemented

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

## Module Hierarchy

Modules should be inserted as a given order. `/` means two modules overrides each other (you can only include one module). `|` means two modules are on the same level and will not override each other.

**DataContainer**: `CSV / Tab > Doc > FileOp > Sequential`

**Parser**: `ParserImpl (specific) > Processing`

## Advanced Usage

Since Tinker is parallel by default, there isn't a seperate parallel module. All parallel operations are already embeded with basic operation modules such as `FileOp`. If you want to switch from parallel to sequential execution, you need to add specific `Sequential` module that's under each module:

```
val doc = new DataContainer("../testCSV.csv", true) with CSV with Doc with FileOp with files.operations.Sequential
```

If you don't want to write long prefixes, you must import as close to the usage as possible. Trait `Sequential` is being defined across multiple modules and repeated namespace can cause the compiler to crash. Remember, Tinker's `Sequential` operation is not as safe and well-tested as parallel operations. Use at your own discretion.

## Customization

```
trait Subtree extends DataContainer with FailureHandle {
  lazy val data = //your way of obtaining data
}

trait VarroSubtreeXML extends FileTypes {
  val headerMap: Option[Array[String]] = None
}

//Use these two modules by calling:

val doc = new DataContainer("../varroXML.xml") with Subtree with VarroSubtreeXML
```

This is an example from `Subtree` folder. You can extend any trait to DataContainer, and start adding new modules and new functions to it.


## Minor Improvements (current release)

1. Now `DataContainer` can treat directory as a file, automatically selecting files with correct suffix names (`.csv` for CSV module, and `.txt` or `.tab` for Tab module)

## Todo

1. Does file output (save) function be asynchronous or synchronous?