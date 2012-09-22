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

import measure.{Duration, Time}
import util.logging.Logger

package object console {

  implicit def string2ConsoleString(string: String) = new ConsoleString(string)

  def timed[T](message: String)(block: => T)(implicit logger: Logger[_]): T = {
    val results = timed[T](block)
    logger.info(message, results._2)
    results._1
  }

  def timed[T](block: => T): (T, Duration) = {
    val before = Time()
    (block, Duration(before, Time()))
  }

}