# Tinker SX

Tinker SX is a concept of what Tinker should look like
in the next release.

It supports pipeline operations, a.k.a method chaining
Concepts and many inspirations are taken from Akka Stream

Tinker SX also uses Better-files as the default file system
and Akka Stream's File IO channel.

```scala
val doc = Doc("../../.csv") ~> CSV ~> Tokenize("mTurkExpression" -> "tokenized_expression") ~> Sentiment("tokenized_expression")
val doc2 = Doc("../../.tab") ~> Tab ~> Drop("gender") ~> Math("age") / 5 + 3 ~> Math("clock_in") + Math("clock_out")
val final_expr = doc ~> Merge(doc2)("name") ~> Save("../../.tab")
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

At last, TinkerSX will offer an easy way to run Breeze/Algebird calculation on your data matrix
without ever losing your other information (planned for future).