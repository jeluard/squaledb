package org.squaledb
package transport
package netty

import serialization.Serializer
import transport.{Message, Response}

/** Specialized [[NettyReceiver]] using a [[InstrumentedSimpleChannelUpstreamHandler]].
 */
abstract class InstrumentedNettyReceiver[I <: Message, O <: Response](port: Int, serializerEncoder: Serializer[I], serializerDecoder: Serializer[O]) extends NettyReceiver[I, O](port, serializerEncoder, serializerDecoder) {

  override protected def createHandler() = new ServerHandler(this) with InstrumentedSimpleChannelUpstreamHandler {
    //override val loggerName = classOf[NettyReceiver[_]].getPackage.getName
    val counterPrefix = "transport.receiver"
  }

}