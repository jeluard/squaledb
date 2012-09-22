/*
 *  Copyright 2011 julien.
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

import support.management.hotspot.performance.Counter
import support.management.hotspot.performance.Counter._

import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.{ChannelHandlerContext, ExceptionEvent, MessageEvent, SimpleChannelUpstreamHandler}

/** [[SimpleChannelUpstreamHandler]] mixin instrumenting some methods to provide performance counter.
 */
trait InstrumentedSimpleChannelUpstreamHandler extends SimpleChannelUpstreamHandler {

  protected val counterPrefix: String
  private [this] final val DefaultMessageReceivedCounterName = counterPrefix+".message-received"
  protected val messageReceivedCounterName = DefaultMessageReceivedCounterName
  lazy private [this] final val messageReceivedCounter = new Counter(messageReceivedCounterName)
  private [this] final val DefaultExceptionCaughtCounterName = counterPrefix+".message-received"
  protected val exceptionCaughtCounterName = DefaultExceptionCaughtCounterName
  lazy private [this] final val exceptionCaughtCounter = new Counter(exceptionCaughtCounterName)

  override abstract def messageReceived(context: ChannelHandlerContext, event: MessageEvent) = counting(messageReceivedCounter) {
    super.messageReceived(context, event)
  }

  override abstract def exceptionCaught(context: ChannelHandlerContext, event: ExceptionEvent) = counting(exceptionCaughtCounter) {
    super.exceptionCaught(context, event)
  }

}