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

import java.io.{ObjectInput, ObjectOutput, InputStream, OutputStream, IOException}

import org.mockito.{Matchers, Mockito}

trait StringObjectOutputInputSerializer extends AbstractObjectOutputInputSerializer[String]

trait ObjectOutputInputSerializerBehaviors extends ByteArraySerializerBehaviors { this : Specification =>

    def objectOutputInputBasedSerializer(serializer: Serializer[String]) = {
        super.byteArrayBasedSerializer(serializer)

        "propagate exception thrown by underlying ObjectOutputStream" in {
            val buggyObjectOutput = mock[ObjectOutput]
            Mockito.doThrow(new IOException()).when(buggyObjectOutput).close

            val serializer = mock[StringObjectOutputInputSerializer]
            Mockito.when(serializer.createObjectOutput(Matchers.any(classOf[OutputStream]))).thenReturn(buggyObjectOutput)
            Mockito.doCallRealMethod().when(serializer).doSerialize(Matchers.any(classOf[String]))

            val value = "value"

            intercept[RuntimeException] {
                serializer.doSerialize(value)
            }
        }

        "propagate exception thrown by underlying ObjectInputStream" in {
            val buggyObjectInput = mock[ObjectInput]
            Mockito.doThrow(new IOException()).when(buggyObjectInput).close

            val failingSerializer = mock[StringObjectOutputInputSerializer]
            Mockito.when(failingSerializer.createObjectInput(Matchers.any(classOf[InputStream]))).thenReturn(buggyObjectInput);
            Mockito.doCallRealMethod().when(failingSerializer).unserialize(Matchers.any(classOf[Array[Byte]]));

            val value = "value"

            intercept[RuntimeException] {
                failingSerializer.unserialize(failingSerializer.doSerialize(value));
            }
        }
    }

}

