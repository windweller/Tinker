package processing

import processing.buffers.BufferConfig
import processing.buffers.file.FileBuffer

/**
 * Created by anie on 5/23/2015
 *
 * Printer works like scheduler
 * use it like
 * new Printer(...) with CSVOutput
 */
abstract class Printer(filePath: Option[String],
                       fileAppend: Boolean = true)
                      (implicit val bufferConfig: BufferConfig = BufferConfig()) extends FileBuffer {

//  this: FileBuffer =>

  override val config: BufferConfig = bufferConfig

  config.filePath = filePath
  config.fileAppend = fileAppend
}