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

import java.io.IOException

package object io {

  type DuckCloseable = AnyRef {def close(): Unit}

  /** Ensures [[Closeable#close]] is called after execution of body.
   *
   *  @example {{{
   *    using org.squaledb.support.io.Closeables._
   *    
   *    val closeable = ...
   *    using (closeable) { closeable =>
   *      ...
   *    }
   *  }}}
   */
  def using[T](closeable: DuckCloseable)(body: DuckCloseable => T): T = try {
    body(closeable)
  } finally {
    closeQuietly(closeable)
  }

  /** Intercepts [[IOException]] eventually thrown by [[DuckCloseable#close]].
   */
  def closeQuietly(closeable: DuckCloseable) = try {
    closeable.close
  } catch {
    case e: IOException => ()
  }

}