/*
 *  Copyright 2010 julien.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.squaledb
package serialization

import support.test.Specification

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, IOException}

import org.mockito.{Matchers, Mockito}

trait StringByteArraySerializer extends AbstractByteArraySerializer[String]

trait ByteArraySerializerBehaviors extends SerializerBehaviors { this : Specification =>

    def byteArrayBasedSerializer(serializer: Serializer[String]) = {
        super.serializer(serializer)

        "propagate exception thrown by underlying OutputStream" in {
            val buggyByteArrayOutputStream = mock[ByteArrayOutputStream]
            Mockito.doThrow(new RuntimeException()).when(buggyByteArrayOutputStream).write(Matchers.anyInt())
            Mockito.doThrow(new IOException()).when(buggyByteArrayOutputStream).write(Matchers.any(classOf[Array[Byte]]))
            Mockito.doThrow(new RuntimeException()).when(buggyByteArrayOutputStream).write(Matchers.any(classOf[Array[Byte]]), Matchers.anyInt(), Matchers.anyInt())

            val serializer = mock[StringByteArraySerializer]
            Mockito.when(serializer.doSerialize(Matchers.any(classOf[String]))).thenCallRealMethod()

            Mockito.when(serializer.createByteArrayOutputStream).thenReturn(buggyByteArrayOutputStream)

            val value = "value"

            intercept[RuntimeException] {
                serializer.doSerialize(value)
            }
        }

        "propagate exception thrown by underlying InputStream" in {
            val buggyByteArrayInputStream = mock[ByteArrayInputStream]
            Mockito.when(buggyByteArrayInputStream.read()).thenThrow(new RuntimeException())
            Mockito.when(buggyByteArrayInputStream.read(Matchers.any(classOf[Array[Byte]]))).thenThrow(new IOException())
            Mockito.when(buggyByteArrayInputStream.read(Matchers.any(classOf[Array[Byte]]), Matchers.anyInt(), Matchers.anyInt())).thenThrow(new RuntimeException())

            val failingSerializer = mock[StringByteArraySerializer]
            Mockito.when(failingSerializer.unserialize(Matchers.any(classOf[Array[Byte]]))).thenCallRealMethod()
            Mockito.when(failingSerializer.createByteArrayInputStream(Matchers.any(classOf[Array[Byte]]))).thenReturn(buggyByteArrayInputStream)

            val value = "value"

            intercept[RuntimeException] {
                failingSerializer.unserialize(failingSerializer.serialize(value))
            }
        }

        "shouldAverageSizeBeGreaterOrEqualsDefault" in {
            val value = "value"

            for (i <- 0 to 10) {
                serializer.serialize(value)
            }

            //serializer.getAverageHistorySize() >= AbstractByteArraySerializer.AVERAGE_DEFAULT
        }

    }

}

