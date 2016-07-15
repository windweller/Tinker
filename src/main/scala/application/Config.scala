package application

import java.io.File

/**
  * Created by aurore on 19/01/16.
  */
case class Config(in: File = new File(""),
                  out: File = new File(""),
                  text: String="",
                  tree: String="parsed",
                  numcore: Int=1,
                  rules: File = new File(""),
                  csv: Boolean = false,
                  tab: Boolean = false,
                  label: Boolean = false,
                  keep: Seq[String] = Seq(),
                  mode: String="",
                  labelplace: String="start",
                  verbose: Boolean = false)
{

}
