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
package control

import util.logging.Logger

import scala.util.control.Exception._

import java.util.logging.Level

/** Helper control methods for [[Exception]]. Rely on [[control#Exception]].
 */
object Exceptions {

  /** Log a specific [[Exception]]. All others are propagated.
   *
   *  @param exception [[Class]] of handled [[Exception]]
   *  @param key key used to log
   *  @param parameters parameters logged
   *  @param block block executed
   *  @param logger [[Logger]] used to log. implicitly resolved.
   */
  final def logging[T](exception: Class[_], key: String, parameters: Any*)(block: => T)(implicit logger: Logger[_]): Option[T] = handling(exception).by { ex =>
    logger.warning(key, ex, parameters); None
  } apply Option(block)

  /** Restore interruption state when an [[InterruptedException]] is received.
   *
   *  @param block block executed
   */
  final def restoring[T](block: => T): Option[T] = handling(classOf[InterruptedException]).by { ex =>
    Thread.currentThread.interrupt; None
  } apply Option(block)

}