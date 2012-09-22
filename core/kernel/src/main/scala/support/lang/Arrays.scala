/*
 *  Copyright 2011 julien.
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
package lang

/** Helper methods for [[Array]].
 */
object Arrays {

  /** @return a new [[Array]] concatenating all specified [[Array]]
   */
  final def concat(arrays: Array[Byte]*) = {
    val length = arrays.map(_.length).sum
    val concate = new Array[Byte](length)
    var position = 0
    for (array <- arrays) {
      val length = array.length
      Array.copy(array, 0, concate, position, length)
      position += length
    }
    concate
  }

  /** @return a slice of specified array defined by from and to position
   */
  final def slice(array: Array[Byte], from: Int = 0, to: Int) = java.util.Arrays.copyOfRange(array, from, to)

  /** @return a slice of specified array defined by from position
   */
  final def slice(array: Array[Byte], from: Int) = java.util.Arrays.copyOfRange(array, from, array.length)

}