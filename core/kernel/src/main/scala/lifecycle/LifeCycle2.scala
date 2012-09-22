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
/*
import support.{Enum, FSM}

trait DefaultLifeCycle extends FSM {

  trait States extends Enum {
    sealed trait EnumVal extends Value with State
  }

  trait Transitions extends Enum {
     sealed trait EnumVal extends Value with Transition
  }

  object DefaultStates extends States {
    val Uninitialized = new EnumVal { val name = "Uninitialized" }
    val Initialized = new EnumVal { val name = "Initialized" }
    val Started = new EnumVal { val name = "Started" }
    val Stopped = new EnumVal { val name = "Stopped" }
    val Closed = new EnumVal { val name = "Closed" }
  }

  object DefaultTransitions extends Transitions {
    import DefaultStates._

    val Initializing = new EnumVal { val name = "Initializing" }
  }

  val states = DefaultStates.values
  val transitions = {
    Uninitialized -> Initialized,
    Initialized -> Started,
    Started -> Stopped,
    Stopped -> Closed
  }

  symetries

}

class Test extends DefaultLifeCycle {

  import DefaultStates._

  entering(Initialized) { from: State =>
    println("Initialized")
  }

  entering(Stopped) { from: State =>
    println("Stopped")
  }

  exiting(Stopped) { from: State =>
    println("Stopped")
  }

  transitioning {
    case _ -> Stopped => println("stopping")
  }

}*/