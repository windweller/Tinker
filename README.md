# Tinker
Tinker is a parallel-by-default File/Directory Management System with additional interface to NLP and ML libraries. Tinker right now employs Scala's notoriously awesome Stackable Trait Pattern, and just finished it's asynchronous File I/O implementation.

## Quick Start - Interactive (REPL)

Start by typing `sbt console` from the root of the project. Currently the Unix/Linux Bash script is broken, but the Windows Batch file works, so if you use windows, you can go to `./dist` and use `tinker.bat` file to start.

Then you would see

```
===============================
Welcome to Tinker 0.1 pre-alpha release
===============================
>
```

Then you can use it like any other Scala REPL from the command line.

## Quick Start - Library

```
import files.filetypes.CSV
import files.filetypes.Tab

  val csv = new DataContainer("././testCSV.csv", header = true) with Doc with CSV
  val tab = new DataContainer("././testTab.tab", header = true) with Doc with Tab
  println(csv.dataIteratorPure.next().mkString("\t"))
```

Stackable trait pattern means you can swap in and out different modules, like playing Lego. If you are dealing with a tab file, you can choose to use `Tab` module instead of `CSV`. Also in the future, you might be able to use `HTML` module or other types of module instead of `Doc` module.

Basic File I/O has a nice high-level abstraction that treats a directory of files and a single file as the same entity, and allow `Iterator` access throught the whole data corpus.

Advanced File I/O is being developed.

Interface to ML and NLP libraries are being developed.
