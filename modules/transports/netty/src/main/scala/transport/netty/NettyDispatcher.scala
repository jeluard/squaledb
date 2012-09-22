package org.squaledb
package transport
package netty

import cluster.node.Identifier
import lifecycle.LifeCycle
import serialization.Serializer
import support.util.logging.{Logger, Logging}
import support.util.concurrent._
import uuid.UUID

import java.net.InetSocketAddress
import java.util.concurrent.{Callable, ConcurrentHashMap, Executors, FutureTask, SynchronousQueue, TimeoutException, TimeUnit}

import org.jboss.netty.buffer.DirectChannelBufferFactory
import org.jboss.netty.bootstrap.ClientBootstrap
import org.jboss.netty.channel.{Channel, ChannelFutureListener, ChannelFuture, ChannelHandlerContext, ChannelPipelineFactory, ChannelStateEvent, Channels, ExceptionEvent, MessageEvent, SimpleChannelUpstreamHandler}
import org.jboss.netty.channel.socket.nio.{NioSocketChannelConfig, NioClientSocketChannelFactory}

object NettyDispatcher {
  val results = new ConcurrentHashMap[UUID, SynchronousQueue[Either[Message, Throwable]]]()
}

class NettyDispatcher[I <: Message, O <: Response](val identifier: Identifier, serializerEncoder: Serializer[I], serializerDecoder: Serializer[O]) extends Dispatcher[I, O] with Unicast with LifeCycle with Logging {

  val workerExecutorCount = Runtime.getRuntime.availableProcessors
  val threadNamePrefix = "NettyDispatcher"
  lazy val channelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(new NamedThreadFactory(threadNamePrefix+" Boss")), Executors.newCachedThreadPool(new NamedThreadFactory(threadNamePrefix+" Worker")), workerExecutorCount)
  lazy val bootstrap = new ClientBootstrap(channelFactory)
  lazy val channel: Channel = {
    val future = bootstrap.connect(new InetSocketAddress(identifier.ip, identifier.port));
    future.awaitUninterruptibly()
    if (future.isSuccess()) {
      future.getChannel()
    } else {
      throw future.getCause
    }
  }
  val connectionTimeout = 5000L
  val responseTimeout = 5L

  protected def createHandler() = new ClientHandler(responseTimeout)

  override def doInitialize() = {
    bootstrap.setOption("tcpNoDelay", false)
    bootstrap.setOption("connectTimeoutMillis", connectionTimeout)
   // bootstrap.setOption(
   //             "child.receiveBufferSizePredictorFactory",
   //             new AdaptiveReceiveBufferSizePredictorFactory(
   //                     64,
   //                     16384,
   //                     65536)
   //             );
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      val handler = createHandler()
      override def getPipeline() = {
        Channels.pipeline(new SerializerEncoder(serializerEncoder), new SerializerDecoder(serializerDecoder), handler)
      }
    })
  }

  override def doStart = {
    channel
  }

  case class InternalException(cause: Throwable) extends Exception(cause)

  protected def notWritable(message: I): Nothing = throw new IllegalStateException("")

  override def send(message: I): O = {
    if (!channel.isWritable()) {
      notWritable(message)
    }

    val id = message.uuid
    val queue = new SynchronousQueue[Either[Message, Throwable]]()
    NettyDispatcher.results.put(id, queue)
    val future = channel.write(message)
    future.addListener(new ChannelFutureListener() {
      override def operationComplete(future: ChannelFuture) = {
        if (!future.isSuccess()) {
          val queue = NettyDispatcher.results.remove(message.uuid)
          //TODO direct exchange call
          queue.offer(Right(new InternalException(future.getCause)), responseTimeout, TimeUnit.SECONDS)
        }
      }
    })
    queue.poll(responseTimeout, TimeUnit.SECONDS) match {
      case Left(result) => result.asInstanceOf[O]
      case Right(InternalException(e)) => throw e.getCause
      case Right(e) => throw e
      case _ => throw new TimeoutException("No response after <"+responseTimeout+"s>")
    }
  }

  override def doStop = {
    channel.close().awaitUninterruptibly
    bootstrap.releaseExternalResources()
  }

}

/** 
 */
class ClientHandler(responseTimeout: Long)(implicit logger: Logger[_]) extends SimpleChannelUpstreamHandler {

  /*override def channelConnected(context: ChannelHandlerContext, event: ChannelStateEvent) = {
    val config = event.getChannel().getConfig.asInstanceOf[NioSocketChannelConfig]
    config.setBufferFactory(new DirectChannelBufferFactory())
    //config.setReceiveBufferSizePredictor(new FixedReceiveBufferSizePredictor(20))
  }*/

  override def messageReceived(context: ChannelHandlerContext, event: MessageEvent) = {
    val message = event.getMessage().asInstanceOf[Message]
    val queue = NettyDispatcher.results.remove(message.uuid)
    if (!queue.offer(Left(message), responseTimeout, TimeUnit.SECONDS)) {
      //Message has been received after responseTimeout. No more client expecting it.
      logger.warning("UNEXPECTED_RESPONSE_RECEIVED")
    }
  }

  override def exceptionCaught(context: ChannelHandlerContext, event: ExceptionEvent) = {
    logger.warning("EXCEPTION_CAUGHT", event.getCause)

    event.getChannel.close()
  }

}