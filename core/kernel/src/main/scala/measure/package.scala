/*
 * Copyright 2010 julien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */

package org.squaledb

import java.io.Closeable
import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture, TimeUnit}

import support.util.concurrent._

package object measure {

  private[this] final def closeable(scheduler: ScheduledFuture[_]) = new Closeable() {
    def close = scheduler.cancel(true)
  }

  /** Schedule `block` for execution at every [[Frequency]].
   */
  final def schedule(frequency: Frequency)(block: => Unit)(implicit executorService: ScheduledExecutorService): Closeable = {
    closeable(executorService.scheduleAtFixedRate(block, 0L, frequency.value, TimeUnit.SECONDS))
  }

  /** Schedule `block` for execution every [[Duration]].
   */
  final def schedule(duration: Duration)(block: => Unit)(implicit executorService: ScheduledExecutorService): Closeable = {
    closeable(executorService.scheduleWithFixedDelay(block, 0L, duration.value, TimeUnit.MILLISECONDS))
  }

}