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
package management

import cluster.membership.ManagerMXBean
import discovery.AggregatorMXBean

import javax.management.MBeanServerConnection

/** [[ManagementFactory]] like for custom [[MBean]].
 */
final object ManagementFactory {

  final val domainName = Root.packageName
  final val discoveryAggregatorMBeanName = string2ObjectName(domainName+":type=discovery,name=Aggregator")
  final val clusterMembershipManagerMBeanName = string2ObjectName(domainName+":type=cluster.membership,name=Manager")
  final def lifecycleMBeanName(name: String) = string2ObjectName(domainName+":type=lifecycle,name="+name)

  final def clusterMembershipManagerMBean(implicit mBeanServerConnection: MBeanServerConnection) = javax.management.JMX.newMXBeanProxy(mBeanServerConnection, clusterMembershipManagerMBeanName, classOf[ManagerMXBean])
  final def discoveryAggregatorMBean(implicit mBeanServerConnection: MBeanServerConnection) = javax.management.JMX.newMXBeanProxy(mBeanServerConnection, discoveryAggregatorMBeanName, classOf[AggregatorMXBean])

}