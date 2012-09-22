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
package discovery

import support.management.{MBeanServers, Name}

/** MXBean interface for [[Aggregator]].
 */
@Name("Aggregator")
trait AggregatorMXBean { self: Aggregator =>

  implicit val domainName = support.management.ManagementFactory.discoveryAggregatorMBeanName
  import MBeanServers._

  MBeanServers.register(this)

  def getNodes: Array[String] = nodes.toArray

  def getNodeTargets(name: String): Array[String] = {
    identifiers(name) match {
      case Some(targets) => targets.map(discoveredNodeTarget => "%s:%s".format(discoveredNodeTarget.ip, discoveredNodeTarget.port)).toArray
      case None => Array[String]()
    }
  }

}