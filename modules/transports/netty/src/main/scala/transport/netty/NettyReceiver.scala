package org.squaledb
package transport
package netty

import lifecycle.LifeCycle
import serialization.Serializer
import support.util.concurrent.NamedThreadFactory
import support.util.logging.{Logger, Logging}
import transport.{Message, Response}

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.buffer.DirectChannelBufferFactory
import org.jboss.netty.channel.{ChannelHandlerContext, ChannelPipelineFactory, ChannelStateEvent, Channels}
import org.jboss.netty.channel.{ExceptionEvent, FixedReceiveBufferSizePredictor, MessageEvent, SimpleChannelUpstreamHandler}
import org.jboss.netty.channel.socket.nio.{NioSocketChannelConfig, NioServerSocketChannelFactory}

object NettyReceiver {
  val allChannels = new CleanupChannelGroup("transport")
}

abstract class NettyReceiver[I <: Message, O <: Response](port: Int, serializerDecoder: Serializer[I], serializerEncoder: Serializer[O]) extends Receiver[I, O] with LifeCycle with Logging {

  val workerExecutorCount = Runtime.getRuntime.availableProcessors
  val threadNamePrefix = "NettyReceiver"
  lazy val channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(new NamedThreadFactory(threadNamePrefix+" Boss")), Executors.newCachedThreadPool(new NamedThreadFactory(threadNamePrefix+" Worker")), workerExecutorCount)
  lazy val bootstrap = new ServerBootstrap(channelFactory)

  override def doInitialize = {
    bootstrap.setOption("child.tcpNoDelay", false)
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      val handler = createHandler()
      override def getPipeline() = Channels.pipeline(new SerializerDecoder(serializerDecoder), new SerializerEncoder(serializerEncoder), handler)
    })
  }

  protected def createHandler() = new ServerHandler[I, O](this)

  override def doStart = bootstrap.bind(new InetSocketAddress(port))

  override def doClose = {
    NettyReceiver.allChannels.close().awaitUninterruptibly
    bootstrap.releaseExternalResources()
  }

  override def toString = "NettyReceiver ("+port+")"

}

class ServerHandler[I <: Message, O <: Response](handler: I => O)(implicit logger: Logger[_]) extends SimpleChannelUpstreamHandler {

  override def channelOpen(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    //Keep track of all opened channels
    NettyReceiver.allChannels.add(e.getChannel)
  }

  /*override def channelConnected(context: ChannelHandlerContext, event: ChannelStateEvent) = {
    val config = event.getChannel().getConfig.asInstanceOf[NioSocketChannelConfig]
    config.setBufferFactory(new DirectChannelBufferFactory())
    //config.setReceiveBufferSizePredictor(new FixedReceiveBufferSizePredictor(20))
  }*/

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    e.getChannel.write(handler(e.getMessage.asInstanceOf[I]))
  }

  override def exceptionCaught(context: ChannelHandlerContext, event: ExceptionEvent) = {
    logger.warning("EXCEPTION_CAUGHT", Option(event.getCause))

    event.getChannel.close()
  }

}