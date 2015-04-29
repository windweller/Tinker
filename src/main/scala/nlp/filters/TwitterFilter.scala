package nlp.filters


import com.github.tototoshi.csv.CSVWriter

import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

/**
 * These are pre-processing filters
 *
 * TwitterFilter is taken from CMU Ark
 * library, it's used to take out URL
 * and emoticons
 *
 * usage guide: .replaceAll(searchPattern.toString(), "")
 */
trait TwitterFilter extends Filter {

  //this produces one csv table with tweet + state name
  def preprocess(saveLoc: String): Unit = {
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    val it = data.iterators.head
    val result = ArrayBuffer.empty[Seq[String]]

    it.foreach { group =>
      val itr = group._2
      while (itr.hasNext) {
        val row = itr.next()
        if (row.nonEmpty) {
          val tweet = row(struct.getTarget).replaceAll(TwitterRegex.searchPattern.toString(), "")
          if (tweet.trim.nonEmpty && tweet.split(" ").length >= 2) {
            result += Seq(struct.getId(row).getOrElse(group._1), tweet)
          }
        }
      }
    }

    output.writeAll(result.toSeq)
  }

  object TwitterRegex {

  val punctChars = "['\"“”‘’.?!…,:;]"
  val entity     = "&(?:amp|lt|gt|quot);"

  //URLs
  val urlStart1  = "(?:https?://|\\bwww\\.)"
  val commonTLDs = "(?:com|org|edu|gov|net|mil|aero|asia|biz|cat|coop|info|int|jobs|mobi|museum|name|pro|tel|travel|xxx)"
  val ccTLDs	 = "(?:ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|" +
    "bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cs|cu|cv|cx|cy|cz|dd|de|dj|dk|dm|do|dz|ec|ee|eg|eh|" +
    "er|es|et|eu|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|" +
    "hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|" +
    "lu|lv|ly|ma|mc|md|me|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|" +
    "nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|rs|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|" +
    "sl|sm|sn|so|sr|ss|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|" +
    "va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|za|zm|zw)"
  val urlStart2  = "\\b(?:[A-Za-z\\d-])+(?:\\.[A-Za-z0-9]+){0,3}\\." + "(?:"+commonTLDs+"|"+ccTLDs+")"+"(?:\\."+ccTLDs+")?(?=\\W|$)"
  val urlBody    = "(?:[^\\.\\s<>][^\\s<>]*?)?"
  val urlExtraCrapBeforeEnd = "(?:"+punctChars+"|"+entity+")+?"
  val urlEnd     = "(?:\\.\\.+|[<>]|\\s|$)"
  val url = "(?:"+urlStart1+"|"+urlStart2+")"+urlBody+"(?=(?:"+urlExtraCrapBeforeEnd+")?"+urlEnd+")"

  //  Emoticons
  val normalEyes = "(?iu)[:=]" // 8 and x are eyes but cause problems
  val wink = "[;]"
  val noseArea = "(?:|-|[^a-zA-Z0-9 ])" // doesn't get :'-(
  val happyMouths = "[D\\)\\]\\}]+"
  val sadMouths = "[\\(\\[\\{]+"
  val tongue = "[pPd3]+"
  val otherMouths = "(?:[oO]+|[/\\\\]+|[vV]+|[Ss]+|[|]+)" // remove forward slash if http://'s aren't cleaned


  // mouth repetition examples:

  val bfLeft = "(♥|0|o|°|v|\\$|t|x|;|\\u0CA0|@|ʘ|•|・|◕|\\^|¬|\\*)"
  val bfCenter = "(?:[\\.]|[_-]+)"
  val bfRight = "\\2"
  val s3 = "(?:--['\"])"
  val s4 = "(?:<|&lt;|>|&gt;)[\\._-]+(?:<|&lt;|>|&gt;)"
  val s5 = "(?:[.][_]+[.])"
  val basicface = "(?:(?i)" +bfLeft+bfCenter+bfRight+ ")|" +s3+ "|" +s4+ "|" + s5


  val eeLeft = "[＼\\\\ƪԄ\\(（<>;ヽ\\-=~\\*]+"
  val eeRight= "[\\-=\\);'\\u0022<>ʃ）/／ノﾉ丿╯σっµ~\\*]+"
  val eeSymbol = "[^A-Za-z0-9\\s\\(\\)\\*:=-]"
  val eastEmote = eeLeft + "(?:"+basicface+"|" +eeSymbol+")+" + eeRight

  val emoticon = (
    "(?:>|&gt;)?" + OR(normalEyes, wink) + OR(noseArea,"[Oo]") +
      OR(tongue+"(?=\\W|$|RT|rt|Rt)", otherMouths+"(?=\\W|$|RT|rt|Rt)", sadMouths, happyMouths),
    "(?<=(?: |^))" + OR(sadMouths,happyMouths,otherMouths) + noseArea + OR(normalEyes, wink) + "(?:<|&lt;)?",
    eastEmote.replaceFirst("2", "1"), basicface
    )

    val nonAscii = "[^\\x00-\\x7F]+"

  def OR(patterns: String*) = {
    patterns.map{p => s"(?:$p)"}.mkString("|")
  }

  val searchPattern = OR(url, emoticon._1, emoticon._2, emoticon._3, emoticon._4, nonAscii).r

  }

}
