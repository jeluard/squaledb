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
package net

import java.net.InetAddress

/** Helper methods for [[InetAddress]].
 */
object InetAddresses {

  /** @return all [[InterfaceAddress]] for this machine.
   */
  def all = {
    InterfaceAddresses.all.map(_.getAddress)
  }

  /** @param address
   *  @return true if provided [[InetAddress]] refers to local machine.
   */
  def isLocal(address: InetAddress) = all.contains(address)

}
