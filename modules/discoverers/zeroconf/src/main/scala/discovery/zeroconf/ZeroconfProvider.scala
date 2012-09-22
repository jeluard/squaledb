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
package discovery
package zeroconf

import lifecycle.{Initializing, LifeCycle}

import javax.jmdns.JmDNS

/** Provides initialization and access to configured [[JmDNS]].
 *
 *  @see http://www.zeroconf.org/
 */
trait ZeroconfProvider extends LifeCycle {

  private var _zeroconfs: Seq[JmDNS] = Seq()

  def zeroconfs = _zeroconfs

  def zeroconfs_= (zeroconfs: Seq[JmDNS]) = _zeroconfs = zeroconfs

  override def doInitialize = doIf(Initializing) {
    //zeroconfs = (for (inetAddress <- NetworkInterfaces.allInetAddresses.filter(inetAddress => {!inetAddress.isMulticastAddress || !inetAddress.isAnyLocalAddress || !inetAddress.isLoopbackAddress})) yield JmDNS.create(inetAddress)).toSeq
    zeroconfs = Seq(JmDNS.create)
  }

  override def doStop = zeroconfs.foreach {zeroconf => zeroconf.close}

}