/**
 * Copyright (C) 2010 julien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.squaledb
package support
package util
package concurrent

import java.util.concurrent.{Executors, ThreadFactory}
import java.util.concurrent.atomic.AtomicLong

/** Default [[ThreadFactory]] returning a named [[Thread]] (daemon by default).
 *
 *  @example {{
 *    val executor = Executors.newCachedThreadPool(new NamedDaemonThreadFactory("name"))
 *  }}
 */
class NamedThreadFactory(name: String, daemon: Boolean = true) extends ThreadFactory {

  private[this] final val count = new AtomicLong
  private[this] final val DefaultThreadName = name+" #"+count.incrementAndGet
  protected def threadName = {
    val currentCount = count.incrementAndGet
    if (currentCount != 1) name else name+" #"+currentCount
  }
  protected val threadFactory = Executors.defaultThreadFactory()

  /** Return a new daemon [[Thread]] with given name.
   */
  def newThread(runnable: Runnable) = {
    val thread = threadFactory.newThread(runnable)
    thread.setDaemon(daemon)
    thread.setName(threadName)
    thread
  }

}