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
package lang

import util.logging.Logging

/** Helper methods for [[Exception]].
 */
final object Exceptions {

  /** [[UncaughtExceptionHandler]] implementation logging all [[Exception]] as [[java.util.logging.Level#SEVERE]].
   */
  object DefaultUncaughtExceptionHandler extends Thread.UncaughtExceptionHandler with Logging {
    def uncaughtException(t: Thread, e: Throwable) = severe("UNCAUGHT_EXCEPTION", e, t.getName)
  }

  /** Install [[DefaultUncaughtExceptionHandler]] as default [[UncaughtExceptionHandler]].
   */
  final def installDefaultUncaughtExceptionHandler = Thread.setDefaultUncaughtExceptionHandler(Exceptions.DefaultUncaughtExceptionHandler)

  /** @return all causes of specified [[Throwable]]
   */
  final def causes(throwable: Throwable) = {
    var causes = List[Throwable]()
    var currentCause = throwable.getCause()
    while (currentCause != null) {
       causes = causes :+ currentCause
       currentCause = currentCause.getCause()
    }
    causes
  }

}