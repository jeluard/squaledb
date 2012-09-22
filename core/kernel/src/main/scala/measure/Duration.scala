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
package measure

import java.util.concurrent.TimeUnit

object Duration {

  def apply(before: Time, after: Time) = new Duration(before, after)

}

case class Duration(val value: Long, val unit: TimeUnit = TimeUnit.MILLISECONDS) {

  def this(before: Time, after: Time) = {
    this(after.value - before.value)
  }

  override def toString = {
    value match {
      case value if value > 1000 => value / 1000 +" seconds and "+value % 1000+" milliseconds"
      case value => value +" milliseconds"
    }
  }

}