/**
 * Copyright (C) 2010 julien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.squaledb
package compression
package zip

import java.io.ByteArrayOutputStream
import java.util.zip.{Deflater, Inflater}

/** [[Compressor]] implementation based on java.util.zip.
 */
class ZIPCompressor(val level: Int = Deflater.BEST_SPEED) extends Compressor {

  val compressor = new Deflater(level)
  val decompressor = new Inflater

  def compress(bytes: Array[Byte]) = {
    compressor.reset()
    compressor.setInput(bytes)
    compressor.finish()

    val byteArrayOutputStream = new ByteArrayOutputStream(bytes.length)
    val buffer = new Array[Byte](bytes.length)
    while (!compressor.finished()) {
      val count = compressor.deflate(buffer)
      byteArrayOutputStream.write(buffer, 0, count)
    }
    byteArrayOutputStream.toByteArray()
  }

  def uncompress(bytes: Array[Byte]) = {
    decompressor.reset()
    decompressor.setInput(bytes)

    val byteArrayOutputStream = new ByteArrayOutputStream(bytes.length)
    val buffer = new Array[Byte](bytes.length)
    while (!this.decompressor.finished()) {
      val count = decompressor.inflate(buffer)
      byteArrayOutputStream.write(buffer, 0, count)
    }
    byteArrayOutputStream.toByteArray()
  }

}