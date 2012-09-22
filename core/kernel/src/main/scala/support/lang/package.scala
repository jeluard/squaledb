/*
 * Copyright 2010 julien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */

package org.squaledb
package support

import uuid.UUID

import java.nio.ByteBuffer

package object lang {

  //TODO allocate vs allocateDirect?
  //TODO Unsafe might help for slicing

  private def allocate(size: Int) = ByteBuffer.allocate(size)

  final def intToBytes(int: Int) = {
    val buffer = allocate(Sizes.Int.bytes)
    buffer.putInt(int)
    buffer.array
  }

  final def bytesToInt(bytes: Array[Byte], offset: Int = 0) = {
    val buffer = ByteBuffer.wrap(bytes, offset, Sizes.Int.bytes)
    buffer.getInt
  }

  final def longToBytes(long: Long) = {
    val buffer = allocate(Sizes.Long.bytes)
    buffer.putLong(long)
    buffer.array
  }

  final def bytesToLong(bytes: Array[Byte], offset: Int = 0) = {
    val buffer = ByteBuffer.wrap(bytes, offset, Sizes.Long.bytes)
    buffer.getLong
  }

  final def uuidToBytes(uuid: UUID): Array[Byte] = {
    val buffer = allocate(Sizes.UUID.bytes)
    appendUUID(uuid, buffer)
    buffer.array
  }

  final def appendUUID(uuid: UUID, buffer: ByteBuffer) {
    buffer.putLong(uuid.bits._1)
    buffer.putLong(uuid.bits._2)
  }

  final def bytesToUUID(bytes: Array[Byte]) = new UUID((bytesToLong(bytes), bytesToLong(bytes, Sizes.Long.bytes)))

}