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
package cluster
package monitoring

import availability.FailureDetector
import lifecycle.LifeCycle
import measure._
import support.util.concurrent._
import support.util.logging.Logging

import java.net.{ConnectException, Socket}
import java.util.concurrent.{Executors, TimeUnit, ScheduledFuture}

import scala.util.Random

//TODO fire events
//TODO as module
/**
 *
 */
class Pinger(manager: Manager, failureDetector: FailureDetector) extends LifeCycle with Logging {

  val threadName = "Pinger"
  val delay = 1L
  implicit val executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(threadName))
  val shutdownTimeout = 10L
  def duration(before: Long) = new Duration(System.currentTimeMillis - before)
  lazy val pinger = schedule(Duration(1000)) {
    val identifier = randomIdentifier
    val before = System.currentTimeMillis
    try {
      //TODO introduce HostReachable, HostUnreachable
      //val address = InetAddress.getByName(url)
      //address.isReachable(10)
      new Socket(identifier.ip, identifier.port)

      failureDetector.requestSucceeded(identifier, Option(duration(before)))

      finest("PING_SUCCEEDED", identifier, duration(before))
    } catch {
      case e: ConnectException => {
        failureDetector.requestFailed(identifier, e, Option(duration(before)))

        //TODO provide exception
        finest("PING_FAILED", e, identifier, duration(before))
      }
    }
  }

  def randomIdentifier = Random.shuffle(manager.nodes).head

  override def doStart = {
    fine("PING_STARTED", delay)

    pinger
  }

  override def doStop = pinger.close()

  override def doClose = shutdownAndAwaitTermination(executorService, shutdownTimeout)

}