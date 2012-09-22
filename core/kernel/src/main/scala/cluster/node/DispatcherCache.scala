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
package cluster
package node

import availability._
import lang.Cache
import lifecycle.LifeCycle
import support.util.logging.Logging
import transport.{Dispatcher, Message, Response}

import collection.mutable.HashMap

class DispatcherCache[I <: Message, O <: Response](resolver: Identifier => Dispatcher[I, O], failureDetector: FailureDetector) extends Cache[Identifier, FailureReportingDispatcher[I, O]] with LifeCycle with Logging {

  private[this] final val failureDetectionListener: AvailabilityChangedEvent => Unit = event => event match {
    case AvailabilityChangedEvent(_, identifier, _, Unreachable | Down) => {
      finest("INVALIDATE", identifier)

      invalidate(identifier)
    }
    case _ =>
  }

  override def doStart = failureDetector.addListener(failureDetectionListener)
  override def doStop = failureDetector.removeListener(failureDetectionListener)

  private[this] final def init(dispatcher: Dispatcher[I, O]) = {
    dispatcher.asInstanceOf[LifeCycle].initialize
    dispatcher.asInstanceOf[LifeCycle].start
  }

  private[this] final def close(dispatcher: Dispatcher[I, O]) = {
    dispatcher.asInstanceOf[LifeCycle].stop
    dispatcher.asInstanceOf[LifeCycle].close
  }

  override def create(identifier: Identifier) = {
    finest("RESOLVE", identifier)

    val dispatcher = resolver(identifier)
    try {
      init(dispatcher)
      new FailureReportingDispatcher(dispatcher, failureDetector)
    } catch {
      case e =>
        failureDetector.requestFailed(identifier, e)
        close(dispatcher)

        throw e
    }
  }

  override def dispose(dispatcher: FailureReportingDispatcher[I, O]) = close(dispatcher.delegate)

}