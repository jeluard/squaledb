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

import lang.Classes._
import util.logging.Logging
import control.Exceptions.logging

import javax.management.{MBeanServer, ObjectName}

/** Helper methods for [[MBeanServer]].
 */
object MBeanServers extends Logging {

  implicit val platformMBeanServer = java.lang.management.ManagementFactory.getPlatformMBeanServer

  def register(any: AnyRef)(implicit name: ObjectName, mBeanServer: MBeanServer) = logging(classOf[Exception], "REGISTRATION_FAILED", any, name) {
    mBeanServer.registerMBean(any, name)

    fine("REGISTRATION_SUCCEEDED", friendlyName(any), name)
  }

  def unregister(any: AnyRef)(implicit name: ObjectName, mBeanServer: MBeanServer) = logging(classOf[Exception], "UNREGISTRATION_FAILED", any, name) {
    mBeanServer.unregisterMBean(name)

    fine("UNREGISTRATION_SUCCEEDED", friendlyName(any), name)
  }

}