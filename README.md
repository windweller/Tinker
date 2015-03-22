# Tinker
Tinker is a parallel-by-default File/Directory Management System with additional interface to NLP and ML libraries. Tinker right now employs Scala's notoriously awesome Stackable Trait Pattern, and just finished it's asynchronous File I/O implementation.

## Quick Start

```
import files.filetypes.CSV
import files.filetypes.Tab

  val doc = new DataContainer("..\\src\\test\\scala\\files\\testFiles\\testCSV.txt", true) with Doc with CSV
  println(doc.dataIteratorPure.next().mkString("\t"))
```

Basic File I/O has a nice high-level abstraction that treats a directory of files and a single file as the same entity, and allow `Iterator` access throught the whole data corpus.

Advanced File I/O is being developed.
