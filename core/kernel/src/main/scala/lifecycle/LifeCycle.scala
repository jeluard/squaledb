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
package lifecycle

abstract class State
case object Uninitialized extends State
case object Initializing extends State
case object Initialized extends State
case object Starting extends State
case object Restarting extends State
case object Started extends State
case object Stopping extends State
case object Stopped extends State
case object Closing extends State
case object Closed extends State

import event.{Event, EventProducer}
import support.lang.Classes._
import support.util.logging.Logger

case class LifeCycleEvent(lifeCycle: LifeCycle, val from: State, val to: State) extends Event(Option(lifeCycle))

//TODO Refactor to make more generic.
//Hierarchy based
//def a = {
//  require 'initialised completed
//  require 'initialised notcompleted
//  require 'initializing current
//}
//DSL based.
//1->2 and 3
//2 -> 4
//4 -> 5
//3 -> 5
//Helper advancing to future phase
//
//Allow delegation to fields (should work for interfaces not implementing LifeCycle but impl does)
//Synetrical operation (ie if start fail, dispose should be called)
/** Basic life cycle mechanism.
 */
trait LifeCycle {

  @volatile var state: State = Uninitialized
  val lifeCycleEventProducer = new EventProducer[LifeCycleEvent]
  val lifeCycleLogger = new Logger(classOf[LifeCycle])

  final def initialize = doIfUninitialized {
      executeThenTransition(Initializing, Initialized) {
          doInitialize
      }
  }

  protected def doInitialize = ()

  final def start = doIf(Initialized, Stopped) {
    val from = state match {
      case Initialized => Starting
      case Stopped => Restarting
    }
    executeThenTransition(from, Started) {
        doStart
    }
  }

  protected def doStart = ()

  final def stop = doIfStarted {
    executeThenTransition(Stopping, Stopped) {
        doStop
    }
  }

  def doStop = ()

  final def close = doIf(Initialized, Stopped) {
    executeThenTransition(Closing, Closed) {
      doClose
    }
  }

  def doClose = ()

  protected final def doIfUninitialized[T](block: => T) = doIf(Uninitialized)(block)

  protected final def doIfInitialized[T](block: => T) = doIf(Initialized)(block)

  protected final def doIfStarted[T](block: => T) = doIf(Started)(block)

  protected final def doIfStopped[T](block: => T) = doIf(Stopped)(block)

  protected final def doIfClosed[T](block: => T) = doIf(Closed)(block)

  protected final def doIf[T](expectedStates: State*)(block: => T): Unit = synchronized {
    expectedStates.foreach {
      expectedState => if (state == expectedState) {
        block
        return
      }
    }
    throw new IllegalStateException("State is not <"+expectedStates.mkString(" or ")+"> but <"+state+">")
  }

  /** Executes associated block. State is changed to *from* before block execution and to *to* after block execution only if no Exception has been fired.
   * 
   *  @param from
   *  @param to
   *  @param block
   */
  protected def executeThenTransition[T](from: State, to: State)(block: => T) = synchronized {
    val actual = state
    transition(from)

    try {
      block
      transition(to)
            
      lifeCycleLogger.finest("LIFECYCLE_TRANSITION_SUCCESS", friendlyName(this), from, to)
    } catch {
      case e => {
        //Restore previous state if execution thrown an exception
        state = actual
        lifeCycleLogger.warning("LIFECYCLE_TRANSITION_FAILURE", e, friendlyName(this), from, to)
        throw e
      }
    }
  }

  protected final def transition(to: State) = synchronized {
    val from = state
    state = to
    lifeCycleEventProducer.fire(LifeCycleEvent(this, from, to))
  }

  final def addLifeCycleListener(listener: LifeCycleEvent => Unit) = lifeCycleEventProducer.addListener(listener)

  final def removeLifeCycleListener(listener: LifeCycleEvent => Unit) = lifeCycleEventProducer.removeListener(listener)

}