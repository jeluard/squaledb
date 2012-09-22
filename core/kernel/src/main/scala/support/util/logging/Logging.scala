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
package util
package logging

import scala.annotation.elidable
import scala.annotation.elidable._

/** Mixin providing [[Logger]] facility.
 *
 * @example {
 *  import support.util.logging.Logging
 *
 *  object Test extends Logging {
 *    def method = {
 *      ...
 *      info("MESSAGE", args)
 *    }
 *  }
 * }
 */
trait Logging {

  implicit protected final val logger = new Logger[this.type]()

  //Message is not passed-by-name as it is always used as a resource bundle key.

  @inline
  @elidable(FINEST)
  protected final def finest(message: String, parameters: Any*) = logger.finest(message, parameters.toArray: _*)
  @inline
  @elidable(FINEST)
  protected final def finest(message: String, t: Throwable, parameters: Any*) = logger.finest(message, t, parameters.toArray: _*)

  @inline
  @elidable(FINER)
  protected final def finer(message: String, parameters: Any*) = logger.finer(message, parameters.toArray: _*)
  @inline
  @elidable(FINER)
  protected final def finer(message: String, t: Throwable, parameters: Any*) = logger.finer(message, t, parameters.toArray: _*)

  @inline
  @elidable(FINE)
  protected final def fine(message: String, parameters: Any*) = logger.fine(message, parameters.toArray: _*)
  @inline
  @elidable(FINE)
  protected final def fine(message: String, t: Throwable, parameters: Any*) = logger.fine(message, t, parameters.toArray: _*)

  @elidable(INFO)
  protected final def info(message: String, parameters: Any*) = logger.info(message, parameters.toArray: _*)
  @elidable(INFO)
  protected final def info(message: String, t: Throwable, parameters: Any*) = logger.info(message, t, parameters.toArray: _*)

  @inline
  @elidable(CONFIG)
  protected final def config(message: String, parameters: Any*) = logger.config(message, parameters.toArray: _*)
  @inline
  @elidable(CONFIG)
  protected final def config(message: String, t: Throwable, parameters: Any*) = logger.config(message, t, parameters.toArray: _*)

  @inline
  @elidable(WARNING)
  protected final def warning(message: String, parameters: Any*) = logger.warning(message, parameters)
  @inline
  @elidable(WARNING)
  protected final def warning(message: String, t: Throwable, parameters: Any*) = logger.warning(message, t, parameters.toArray: _*)

  @inline
  @elidable(SEVERE)
  protected final def severe(message: String, parameters: Any*) = logger.severe(message, parameters.toArray: _*)
  @inline
  @elidable(SEVERE)
  protected final def severe(message: String, t: Throwable, parameters: Any*) = logger.severe(message, t, parameters.toArray: _*)

}