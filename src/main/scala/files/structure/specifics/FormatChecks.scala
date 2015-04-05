package files.structure.specifics

import files.structure.DataStructureTypes.Structure
import files.structure.{DataStructureValue, DataStructure}
import utils.FailureHandle

/**
 * Created by anie on 3/25/2015.
 *
 * DataStructure will invoke check() function
 * to examine the validity of the format
 * when the class is created
 *
 * FormatChecks is the high level trait
 * that allows specific traits to check
 * the format of specific data structure
 */
trait FormatChecks extends FailureHandle{
  //fail the system is the test is not passed
  def check(st: Structure): Unit = {
    fail("You haven't mixed in a concrete specific format. Default check does not check anything.")
  }



}
