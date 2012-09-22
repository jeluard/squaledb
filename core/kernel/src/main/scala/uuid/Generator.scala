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
package uuid

/** Abstract generation/parsing of [[UUID]].
 */
trait Generator extends Function0[UUID] {

  import Version._

  /** @return `true` if [[Version]] is supported
   */
  def supports(version: Version): Boolean

  /** @return [[UUID]] parsed from specified [[String]] representation
   */
  def parse(string: String): UUID

}