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

import control.Exceptions._
import util.logging.Logger

/** Helper methods for all collections.
 */
package object collection {

  implicit def iterable2RichIterable[T](iterable: Iterable[T]) = new RichIterable[T](iterable)

  /** Add methods to [[Iterable]].
   */
  class RichIterable[T](iterable: Iterable[T]) {

    /** Apply `block` to all elements. Log if an execution throws and [[Exception]] and continue with next element.
     */
    final def safeForeach[U](message: String, parameters: Any*)(block: T => U)(implicit logger: Logger[_]) = iterable.foreach { value =>
      logging(classOf[Throwable], message, parameters) {
        block(value)
      }
    }

    /** Create a [[String]] representation by applying `block` to each element.
     */
    final def toString(beginning: String = "", separator: String = ", ", end: String = ".")(block: T => String) = {
      val builder = new StringBuilder(beginning+block(iterable.head))
      iterable.tail.foreach(element => builder.append(separator+block(element)))
      builder.append(end).toString
    }
  }

}