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
package java

/** Serializer implementation based on regular Java serialization.
 *
 *  @see ObjectInput
 *  @see ObjectOutput
 *  @see http://java.sun.com/javase/6/docs/platform/serialization/spec/serialTOC.html
 */
class JavaSerializer[T] extends AbstractObjectOutputInputSerializer[T] {

  override def doSerialize(any: T): Array[Byte] = {
    val byteArrayOutputStream = createByteArrayOutputStream
    val objectOutput = createObjectOutput(byteArrayOutputStream)
    objectOutput.writeObject(any)

    try {
      byteArrayOutputStream.toByteArray
    } finally {
      objectOutput.close//Closes underlying stream
    }
  }

  def unserialize(bytes: Array[Byte]): T = {
    val byteArrayInputStream = createByteArrayInputStream(bytes)
    val objectInput = createObjectInput(byteArrayInputStream)

    try {
      objectInput.readObject.asInstanceOf[T]
    } finally {
      objectInput.close//Closes underlying stream
    }
  }

}