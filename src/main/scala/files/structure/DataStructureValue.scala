package files.structure

import files.structure.specifics.FormatChecks

/**
 * Created by anie on 4/5/2015
 *
 * This class is more associated with each row
 * used to provide additional info
 */
case class DataStructureValue(var idValue: Option[String] = None,
                               var labelValue: Option[String] = None,
                               var attributeValues: Option[Vector[String]] = None) extends FormatChecks {

  check(Right(this))
}
