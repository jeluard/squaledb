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
package event

import support.collection._
import support.util.logging.Logger
import support.util.concurrent.locks._

import java.util.concurrent.locks.ReentrantReadWriteLock

/** Abstracts event firing/listening mechanism.
 *
 *  @example {{{
 *    case class MyEvent(override val source: MyProducer) extends Event[MyProducer](source)
 *
 *    class MyProducer extends EventProducer[MyEvent] {
 *        def test = fire(new MyEvent(this))
 *    }
 *
 *    val producer = new MyProducer
 *    producer.addListener((event: MyEvent) => println(event))
 *    producer.test
 *  }}}
 */
class EventProducer[T <: Event] {

  private[this] implicit final val eventProducerLogger = new Logger(classOf[EventProducer[_]])
  private[this] var listeners: List[T => Unit] = Nil
  private[this] final val listenersLock = new ReentrantReadWriteLock

  /**
   * @return true if listener is already registered
   */
  private[this] final def isRegistered(listener: T => Unit) = listeners.exists(_==listener)

  /** Adds a listener to registered listeners.
   *
   *  @param listener to unregister
   *  @throws IllegalArgumentException if provided listener is already registered
   */
  final def addListener(listener: T => Unit) = writeLocking(listenersLock) {
    require(!isRegistered(listener), "Already registered")

    listeners = listener :: listeners
  }

  /** Removes a listener from registered listeners.
   *
   *  @param listener to unregister
   *  @throws IllegalArgumentException if provided listener isn't registered
   */
  final def removeListener(listener: T => Unit) = writeLocking(listenersLock) {
    require(isRegistered(listener), "Not registered")

    listeners = listeners.filter(_!=listener)
  }

  /** Applies given event to all registered listeners.
   *  An Exception thrown by one of the listener do not prevent others to be executed.
   *
   *  @param event to fire
   */
  final def fire(event: T) = writeLocking(listenersLock) {
    listeners.safeForeach("EXCEPTION_WHILE_FIRING") { listener =>
      listener.apply(event)
    }
  }

}