package application

import java.io.File


/**
  * Created by aurore on 19/01/16.
  */
case class Config(in: File = new File("."), out: File = new File("."),
                  rules: File = new File("."),
                  clean: Boolean = false, parse: Boolean = false, matched: Boolean = false)
{

}
