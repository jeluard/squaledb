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
package transport

import support.management.hotspot.performance.Counter
import support.management.hotspot.performance.Counter._

/** [[Dispatcher]] mixin instrumenting [[Dispatcher#send(I)]].
 */
trait InstrumentedDispatcher[I <: Message, O <: Response] extends Dispatcher[I, O] {

  import sun.management.counter.Units.TICKS

  private [this] final val DefaultCounterName = "transport.dispatcher.message-sent"
  protected val counterName = DefaultCounterName
  private [this] final val messageSentCounter = new Counter(counterName, units = TICKS)

  override abstract def send(message: I): O = counting(messageSentCounter) {
    super.send(message)
  }

}