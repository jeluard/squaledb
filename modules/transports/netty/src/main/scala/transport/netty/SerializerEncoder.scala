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
package transport
package netty

import serialization.Serializer
import support.lang.Sizes

import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.{Channel, ChannelHandlerContext}
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder

/** [[OneToOneEncoder]] relying on [[Serializer]].
 */
class SerializerEncoder[T <: AnyRef](val serializer: Serializer[T]) extends OneToOneEncoder {

  override def encode(ctx: ChannelHandlerContext, channel: Channel, any: AnyRef) = {
    val bytes = serializer.serialize(any.asInstanceOf[T])
    val size = Sizes.Int.bytes + bytes.length
    val buffer = ChannelBuffers.buffer(size)
    buffer.writeInt(bytes.length)
    buffer.writeBytes(bytes)
    buffer
  }

}