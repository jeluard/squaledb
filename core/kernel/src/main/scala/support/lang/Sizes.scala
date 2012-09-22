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

/** Size for common primitive types.
 */
object Sizes {

  sealed case class Primitive(bits: Int) {
    val bytes = bits / java.lang.Byte.SIZE
  }

  final object Byte extends Primitive(java.lang.Byte.SIZE)
  final object Short extends Primitive(java.lang.Short.SIZE)
  final object Char extends Primitive(java.lang.Character.SIZE)
  final object Int extends Primitive(java.lang.Integer.SIZE)
  final object Float extends Primitive(java.lang.Float.SIZE)
  final object Long extends Primitive(java.lang.Long.SIZE)
  final object Double extends Primitive(java.lang.Double.SIZE)
  final object UUID extends Primitive(2*java.lang.Long.SIZE)

}