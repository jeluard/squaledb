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
package support
package control

import scala.util.control.ControlThrowable
import scala.util.control.Breaks._

/** A class that can be instantiated for the retry control abstraction.
 *
 *  Enclosed block will be re-executed until it does not throw an exception.
 *  #abandon will stop execution at the call point. #retry will explicitly re-execute provided block.
 *
 *  A maximum retry count can be provided which will stop execution by throwing a RetryLimitBreachedException when breached.
 *
 *
 *  @example {{{
 *    val retries = new Retries
 *    import</b> retries.{abandon, retry, retryable}
 *
 *    retryable { count: Int =>
 *      for (...) {
 *        try {
 *          ...
 *        } catch {
 *          case e: Error => abandon
 *          case e: Exception => {
 *            ...
 *            retry
 *          }
 *        }
 *      }
 *    }
 *  }}}
 *
 *  @see [[Breaks]]
 */
object Retries {

  private[this] final val retryException = new RetryControl
  private[this] final val abandonException = new AbandonControl

  final def retryable[T](block: Int => T): Option[T] = retryable(Int.MaxValue)(block)

  /** A block from which one can exit with [[#abandon]] or re-execute with [[#retry]].
   *
   *  @param maxRetryCount before giving up
   */
  final def retryable[T](maxRetryCount: Int = Int.MaxValue)(block: Int => T): Option[T] = try {
    var result: Option[T] = None
    breakable {
      0.until(maxRetryCount).foreach { retryCount =>
        try {
          result = Option(block(retryCount))
          break
        } catch {
          case e: RetryControl => break
        }
      }
    }
    result
  } catch {
    case e: AbandonControl => None
  }

  /** Retry dynamically closest enclosing retryable block
   *
   *  @note this might be different than the statically closest enclosing block!
   */
  protected final def retry = throw retryException

  /** Abandon dynamically closest enclosing retryable block
   *
   *  @note this might be different than the statically closest enclosing block!
   */
  protected final def abandon = throw abandonException

}

//object Retries extends Retries

private final class RetryControl extends ControlThrowable
private final class AbandonControl extends ControlThrowable

final class RetryLimitBreachedException(val retryCount: Int) extends RuntimeException("Retried attempts exceeded maximum allowed of <"+retryCount+">")