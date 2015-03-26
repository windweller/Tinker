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

## Library

When you are using Tinker as a library, there are several components to it. Here is what they are:

**DataStructure**: Data structure is a standalone marker class that allows user to freely mark down the data format as they see fit. We do not automatically generate those from the data file due to speed and flexibility issue (think about R's horrible speed issue, and how you have to painfully cast strings to factors or factors to strings). We do not check if a `DataStructure` matches with a file until you start using it with a real `DataContainer` file.

