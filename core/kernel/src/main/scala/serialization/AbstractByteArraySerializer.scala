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

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.LinkedList

import java.util.concurrent.locks.ReentrantLock;

/** Base Serializer implementation for ByteArrayOutputStream based Serializer.
 *
 *  Sizes created ByteArrayOutputStream based on the average size of last HISTORY_SIZE sizes.
 */
abstract class AbstractByteArraySerializer[T] extends Serializer[T] {

    var count = 1
    var averageSize = defaultAverageSize
    val defaultAverageSize = 32
    
    val sizesHistory = new LinkedList[Int];
    val lock = new ReentrantLock

    final def createByteArrayOutputStream = {
        new ByteArrayOutputStream(averageSize)
    }

    final def createByteArrayInputStream(bytes: Array[Byte]) = {
        new ByteArrayInputStream(bytes)
    }

    def serialize(any: T): Array[Byte] = {
        val bytes = doSerialize(any)

        lock.lock();
        try {
            averageSize = (averageSize * count + bytes.length) / count
            count = count + 1
        } finally {
            lock.unlock();
        }
        return bytes;
    }

    def doSerialize(any: T): Array[Byte]

}
