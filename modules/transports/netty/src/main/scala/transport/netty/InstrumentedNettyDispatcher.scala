package org.squaledb
package transport
package netty
import cluster.node.Identifier
import serialization.Serializer

/** Specialized [[NettyDispatcher]] using a [[InstrumentedSimpleChannelUpstreamHandler]].
 */
class InstrumentedNettyDispatcher[I <: Message, O <: Response](identifier: Identifier, serializerEncoder: Serializer[I], serializerDecoder: Serializer[O]) extends NettyDispatcher[I, O](identifier, serializerEncoder, serializerDecoder) {

  override protected def createHandler() = new ClientHandler(responseTimeout) with InstrumentedSimpleChannelUpstreamHandler {
    //override val loggerName = classOf[NettyDispatcher[_, _]].getPackage.getName
    val counterPrefix = "transport.dispatcher"
  }

}