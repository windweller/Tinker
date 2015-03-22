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

  val doc = new DataContainer("..\\src\\test\\scala\\files\\testFiles\\testCSV.txt", true) with Doc with CSV
  println(doc.dataIteratorPure.next().mkString("\t"))
```

Basic File I/O has a nice high-level abstraction that treats a directory of files and a single file as the same entity, and allow `Iterator` access throught the whole data corpus.

Advanced File I/O is being developed.
