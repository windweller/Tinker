package application

import java.io.File

/**
  * Created by aurore on 19/01/16.
  */
case class Config(in: File = new File(""), out: File = new File(""),
                  namecolumn: String="", numcore: Int=1,
                  rules: File = new File(""),
                  csv: Boolean = false, tab: Boolean = false,
                  mode: String="")
{

}
