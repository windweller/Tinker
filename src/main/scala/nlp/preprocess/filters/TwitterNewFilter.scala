package nlp.preprocess.filters

import application.Application
import com.github.tototoshi.csv.CSVWriter
import utils.Timer

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * These are pre-processing filters
 *
 * TwitterNewFilter is taken from CMU Ark
 * library, it's used to take out URL
 * ,emoticons and twitter mentions
 *
 * usage guide: .replaceAll(searchPattern.toString(), "")
 *
 * It will also change forms, (like duh to the), changing
 * from a mispelling dictionary. (See : TwitterMispellingFilter.scala)
 *
 */
trait TwitterNewFilter extends Filter {

  val doc = scala.io.Source.fromURL(Application.file("mispellingDic.txt"))
    .getLines()
  var mispellingdict = mutable.HashMap.empty[String, String]

  doc.foreach { line =>
    val parts = line.split("->")
    val value = parts(0).trim
    val keys = parts(1).split("\\|")
    keys.foreach(k => mispellingdict.put(k.trim, value))
  }

  //this produces one csv table with tweet + state name
  def preprocess(saveLoc: String): Unit = {
    val output: CSVWriter = CSVWriter.open(saveLoc, append = true)

    val it = data.data
    val result = ArrayBuffer.empty[Seq[String]]

    it.foreach { row =>
      if (row.get(struct.target.get).nonEmpty) {
        val mispelledTweet = row(struct.target.get).replaceAll(TwitterRegex.searchPattern.toString(), "")
        if (mispelledTweet.trim.nonEmpty && mispelledTweet.split(" ").length >= 2) {
          val keep = struct.getKeepColumnsValue(row)
          val tweet = mispelledTweet.split(" ").map { word =>
            val replaceWord = mispellingdict.get(word)
            replaceWord.getOrElse(word)
          }.mkString(" ")
          val seqnormal = Seq(struct.getIdValue(row).getOrElse(row("file_name")), tweet)
          if (keep.isEmpty)
            result += seqnormal
          else
            result += seqnormal ++ keep.get
          if(Application.verbose) Timer.completeOne
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

    //Emoticon
    val eyes = "[^\\w][:=;xX8]"
    val nose = "-?"
    val mouth = "(?:[D\\)\\]\\}\\(\\[\\{pPd3oO/\\\\vVSs|]+)"
    val l2r = eyes + nose + mouth
    val r2l = mouth + nose + eyes

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
    l2r, r2l,
    eastEmote.replaceFirst("2", "1"), basicface
    )

    val nonAscii = "[^\\x00-\\x7F]+"
  val searchPattern = OR(url, nonAscii, emoticon._1, emoticon._2, emoticon._3, emoticon._4).r

  def OR(patterns: String*) = {
    patterns.map{p => s"(?:$p)"}.mkString("|")
  }
  }

}
