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
package measure

/** Represents a percentage measure.
 */
case class Percentage(val value: Int) extends Ordered[Percentage] {

  require(value >= 0 && value <= 100, "Value must be >= 0 and <= 100")

  def compare(that: Percentage) = value.compare(that.value)

  override def toString = value+"%"

}