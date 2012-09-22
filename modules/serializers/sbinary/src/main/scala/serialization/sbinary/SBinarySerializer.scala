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
package serialization
package sbinary

import _root_.sbinary._
import _root_.sbinary.DefaultProtocol._
import _root_.sbinary.JavaIO._
import _root_.sbinary.Operations._

/** [[Serializer]] implementation using sbinary (http://github.com/harrah/sbinary).
 */
abstract class SBinarySerializer[T](implicit val format: Format[T]) extends AbstractByteArraySerializer[T] {

  def doSerialize(any: T): Array[Byte] = {
    val byteArrayOutputStream = createByteArrayOutputStream
    format.writes(byteArrayOutputStream, any)
    try {
      return byteArrayOutputStream.toByteArray
    } finally {
      byteArrayOutputStream.close
    }
  }

  def unserialize(bytes: Array[Byte]): T = {
    val byteArrayInputStream = createByteArrayInputStream(bytes)
    try {
      return format.reads(byteArrayInputStream).asInstanceOf[T]
    } finally {
      byteArrayInputStream.close
    }
  }

}