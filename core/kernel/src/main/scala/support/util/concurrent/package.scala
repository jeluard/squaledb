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
package support
package util

import java.util.concurrent.{Callable, ExecutorService, TimeUnit}

package object concurrent {

  /** Convert a function returning [[Unit]] to a [[Runnable]].
   */
  implicit final def function2Runnable(f: => Unit) = new Runnable() { def run() = f }

  /** Convert a function returning [[T]] to a [[Callable[T]].
   */
  implicit final def function2Callable[T](f: => T) = new Callable[T]() { def call() = f }

    /** Shutdowns an [[ExecutorService]] by letting up to timeout to finish execution of pending tasks.
   * 
   *  @param executorService
   *  @param timeout time to wait in seconds.
   */
  final def shutdownAndAwaitTermination(executorService: ExecutorService, timeout: Long) = {
    executorService.shutdown
    try {
      val halfTimeout = timeout / 2
      if (!executorService.awaitTermination(halfTimeout, TimeUnit.SECONDS)) {
        executorService.shutdownNow
        executorService.awaitTermination(halfTimeout, TimeUnit.SECONDS)
      }
    } catch {
      case e:InterruptedException => {
        executorService.shutdownNow
        Thread.currentThread.interrupt
      }
    }
  }

}