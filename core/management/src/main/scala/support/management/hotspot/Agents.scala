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

import com.sun.tools.attach.VirtualMachine

import java.io.File

/** Helper methods for Agent.
 *
 *  @see http://download.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html
 */
object Agents {

  requireHotSpot

  /** Load monitoring agent in a [[VirtualMachine]].
   */
  def loadMonitoringAgent(options: String, virtualMachine: VirtualMachine = VirtualMachines.local) = {
    val home = virtualMachine.getSystemProperties().getProperty("java.home")
    val agent = home + File.separator + "lib" + File.separator + "management-agent.jar"
    //val virtualMachine = VirtualMachines.local
    try {
      virtualMachine.loadAgent(agent, options)
    } finally {
      virtualMachine.detach
    }
  }

}
