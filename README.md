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
import files.filetypes._
import parser._

val doc = new DataContainer("E:\\Allen\\Tinker\\src\\test\\scala\\files\\testFiles\\testCSV.csv", true) with CSV with Doc
val parser = new Parser(doc) with CSV with FileBuffer with TregexMatcher with Processing
```

Stackable trait pattern means you can swap in and out different modules, like playing Lego. If you are dealing with a tab file, you can choose to use `Tab` module instead of `CSV`. Also in the future, you might be able to use `HTML` module or other types of module instead of `Doc` module.

Basic File I/O has a nice high-level abstraction that treats a directory of files and a single file as the same entity, and allow `Iterator` access throught the whole data corpus. The orders of modules are important. General rule of thumb: more specific modules go first (such as `CSV` for `Tab`) before general module (such as `Doc`). They contain information later modules will invoke. Operation modules go last such as `FileOp`.

Advanced File I/O is being developed.

Interface to ML and NLP libraries are being developed.

## Modules

`FileBuffer`: This is a module you call when you are attaching an operational module to your algorithm or data container. This module is almost mandatory (which means there's a chance it might just get absorbed/integrated into the main module) for any processing, becasuse of Tinker's delayed execution pattern.

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

1. `Tab` now still generates lines in random order (probably a problem with Java NIO)