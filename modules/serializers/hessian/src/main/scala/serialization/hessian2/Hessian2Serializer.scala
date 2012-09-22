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
package hessian2

import com.caucho.hessian.io.{Hessian2Input,  Hessian2Output}

/** [[Serializer]] implementation using Hessian serialization (http://hessian.caucho.com/).
 */
class Hessian2Serializer[T] extends AbstractByteArraySerializer[T] {

  def doSerialize(any: T): Array[Byte] = {
    val byteArrayOutputStream = createByteArrayOutputStream
    val output = new Hessian2Output(byteArrayOutputStream)
    output.writeObject(any)
    output.close

    try {
      return byteArrayOutputStream.toByteArray
    } finally {
      byteArrayOutputStream.close
    }
  }

  def unserialize(bytes: Array[Byte]): T = {
    val byteArrayInputStream = createByteArrayInputStream(bytes)
    val input = new Hessian2Input(byteArrayInputStream)

    try {
      return input.readObject.asInstanceOf[T]
    } finally {
      input.close
      byteArrayInputStream.close
    }
  }

}