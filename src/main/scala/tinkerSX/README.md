# Tinker SX

Tinker SX is a concept of what Tinker should look like
in the next release.

It supports pipeline operations, a.k.a method chaining
Concepts and many inspirations are taken from Akka Stream

Tinker SX also uses Better-files as the default file system
and Akka Stream's File IO channel.

```scala
val doc = Doc("../../.csv") ~> CSV ~> Tokenize("mTurkExpression" -> "tokenized_expression") ~> Sentiment("tokenized_expression")
val final_expr = doc ~> Merge(doc2)("name") ~> Save("../../.tab", Output.Tab)
final_expr.eval()

```

But we also want to be able to manipulate each column,
since every column is a random variable x.

```scala
val expr = doc["test_score"] ~> MiniMax
val expr2 = doc["test_score"] ~> Normalize

```

You can also remove rows by:

```scala
val expr = doc ~> RemoveRow(Criteria.Incomplete) ~> RemoveColumn("age")

//RemoveRow also takes in an ananymous function like this:

val expr doc ~> RemoveRow(row => row["age"] >= 10)

```

Everytime Tinker will return an expression/operation that you eventually evaluate.
This guarantees that everything is immutable, no change actually happens on the data level.

You can easily try different and even conflicting operations on the same dataset.   

Also we allow column-level operations such as Combination:

```scala
val doc2 = Doc("../../.tab") ~> Tab ~> Drop("gender") ~> Add("rowA", "rowB")

```

At last, TinkerSX will offer an easy way to run Breeze/Algebird calculation on your data matrix
without ever losing your other information (planned for future).

====================

In theory, `~>` is a method that's defined somewhere and takes a class of Operation.

Builder should hold expressions inside

but the first step, parsing document into TypedRow is not the same as following steps.

So Doc must also define a special `~>` function that triggers parsing, and takes in 
either CSV or Tab or a File Format (even database format class)

So the flow looks like:

`String ~> TypedRow ~> .... ~> TypedRow`

When saving it to file, we actually pass in an instantiated class that's stored in `Output.Tab`

Then how to realize `.eval()`? It's built into the expression.

Expression stores operations, and operation contains method that converts itself to
an Akka Flow. Otherwise operation stores functions.

In order to combat the problem of parsing (which will expand rows)
All TypedRow must now have the ability to contain a list of rows.

One TypedRow regularly contains one row...but in case of expansion, it will take more.