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
package concurrent

import java.util.concurrent.locks.{Lock, ReadWriteLock}

package object locks {

  /** Execute `block` while read-locking provided [[ReadWriteLock]].
   */
  final def readLocking(lock: ReadWriteLock)(block: => Unit) = locking(lock.readLock)(block)

  /** Execute `block` while write-locking provided [[ReadWriteLock]].
   */
  final def writeLocking(lock: ReadWriteLock)(block: => Unit) = locking(lock.writeLock)(block)

  /** Execute `block` while locking provided [[Lock]].
   */
  final def locking(lock: Lock)(block: => Unit) = {
    lock.lock
    try {
      block
    } finally {
      lock.unlock
    }
  }

}