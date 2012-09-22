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
package lang

/** Finite State Machine implementation.
 *  Does not support optimization.
 *
 *  @see http://en.wikipedia.org/wiki/Finite-state_machine
 */
trait FSM {

  type State
  type Transition
  type TransitionMatcher = PartialFunction[State => State, Unit]

  def startState
  /** Current [[State]] if defined. Undefined during a [[Transition]].
   */
  def currentState = Option(startState)
  def finalStates

  //private val enteringActions = mutable.Map[S, StateFunction]()

  protected def entering(state: State)(action: State => Unit) = {
    require(state != startState, "")

  }

  protected def exiting(state: State)(action: State => Unit) = {
  }

  protected def transitioning(block: TransitionMatcher) = {
    
  }

  def transition(to: State) = {
    //Check if ok
    //Execute all logic
    //current state -> None
 //   notifyExitingHandlers(current, to)
 //   notifyTransitionHandlers
 //   notifyEnteringHandlers(current, to)
  }

}