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

import java.lang.management.ManagementFactory

/** Helper methods for [[VirtualMachine]].
 *  Relies on attach mechanism to access a VM. Can be disabled by setting '-XX:+DisableAttachMechanism'.
 */
object VirtualMachines {

  /** PID of local VM.
   */
  val localPID = ManagementFactory.getRuntimeMXBean.getName.split('@')(0)

  /** [[VirtualMachine]] pointing to local VM.
   */
  val local = VirtualMachine.attach(localPID)

}
