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
package cluster
package membership

import support.management.{MBeanServers, Name}

/** MXBean interface for [[Manager]].
 */
@Name("Manager")
trait ManagerMXBean { self: Manager =>

  implicit val  domainName = support.management.ManagementFactory.clusterMembershipManagerMBeanName
  import MBeanServers._

  MBeanServers.register(this)

  def allNodes = self.nodes.foreach(_.toString())

  def register(node: String) = {
  }

  def unregister(node: String) = {
  }

}