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
package support
package management
package hotspot
package performance

import control.Retries._
import sun.management.counter.{Units, Variability}

import java.nio.{ByteBuffer, ByteOrder}
import java.util.concurrent.atomic.AtomicLong

object Counter {

  /** Increment [[Counter]] each invocation.
   */
  def counting[T](counter: Counter)(body: => T): T = try {
    body
  } finally {
    counter.increment()
  }

}

/** A hotspot performance counter.
 *  Can be monitored via `jstat -J-Djstat.showUnsupported=true -snap -name squaledb.* PID
 *
 *  No buffer overflow risks here.
 */
class Counter(name: String, variability: Variability = Variability.MONOTONIC, units: Units = Units.TICKS) {

  require(name.contains("."), "Counter name <"+name+"> must include a '.'")

  private [this] final val value = new AtomicLong(0)
  private [this] final val buffer: ByteBuffer = retryable { count: Int =>
    val counterName = Root.packageName+"."+name
    val fullCounterName = if (count == 0) counterName else counterName+" #"+count
    perf.createLong(fullCounterName, variability.intValue, units.intValue, value.get)
    //buffer.order(ByteOrder.nativeOrder())
  }.getOrElse(throw new IllegalArgumentException("Cannot create Counter with name <"+name+">"))

  /** Update counter to value.
   */
  private [this] def update(value: Long) {
    buffer.putLong(value)
    buffer.rewind
  }

  /** Add '1' to counter value.
   */
  final def increment() = update(value.incrementAndGet())

  /** Update counter value to '0'.
   */
  final def reset() = synchronized {
    val zero = 0
    value.set(zero)
    update(zero)
  }

}