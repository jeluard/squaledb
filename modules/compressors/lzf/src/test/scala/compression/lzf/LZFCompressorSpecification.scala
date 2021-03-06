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
package lzf

import support.test.Specification

class LZFCompressorSpecification extends Specification with CompressorBehaviors {

  val createdCompressor = new LZFCompressor()
    
  "A LZFCompressor" when {

      "created" should {
          behave like compressor(createdCompressor)
      }

  }

}