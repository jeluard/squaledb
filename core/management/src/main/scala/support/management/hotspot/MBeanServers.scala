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
package hotspot

/** Helper methods for [[MBeanServer]].
 */
object MBeanServers {

  requireHotSpot

  //See JDK_HOME/lib/management/management.properties for all properties
  val localMonitoringProperty = "com.sun.management.jmxremote"
  val remoteMonitoringPortProperty = "com.sun.management.jmxremote.port"
  val remoteMonitoringAuthenticateProperty = "com.sun.management.jmxremote.authenticate"
  val remoteMonitoringSSLProperty = "com.sun.management.jmxremote.ssl"

  /** Enable local monitoring agent. Platform [[MBeanServer]] will be locally accessible via JMX.
   */
  def enableLocalMonitoring = {
    Agents.loadMonitoringAgent(localMonitoringProperty+"=true")
  }

  /** Enable local monitoring agent. Platform [[MBeanServer]] will be remotely accessible via JMX. No security is configured.
   */
  def enableUnsecureRemoteMonitoring(port: Int) = {
    Agents.loadMonitoringAgent(List(
        remoteMonitoringPortProperty+"="+port.toString,
        remoteMonitoringAuthenticateProperty+"="+false,
        remoteMonitoringSSLProperty+"="+false
      ).mkString(","))
  }

}
