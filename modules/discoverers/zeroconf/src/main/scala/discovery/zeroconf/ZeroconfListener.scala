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
package zeroconf

import cluster.node.{Node, Identifier}
import support.net.InetAddresses
import support.util.logging.Logging

import java.net.InetAddress

import javax.jmdns.{ServiceEvent, ServiceListener}

/** Zeroconf based [[Listener]].
 */
class ZeroconfListener extends Listener with ZeroconfProvider with Logging {

  import support.control.Exceptions._

  val serviceListener = new ServiceListener {

    final def isSelfDiscovery(address: InetAddress, port: Int) = InetAddresses.isLocal(address) && port == Node.identifier.port

    final def filterSelfDiscovery(serviceEvent: ServiceEvent)(block: Identifier => Unit) = if (!isSelfDiscovery(serviceEvent.getInfo.getInetAddresses.head, serviceEvent.getInfo.getPort)) {
      //We are not interested in discovering ourselves.
      for (address <- serviceEvent.getInfo.getInetAddresses) block(createNodeIdentifier(serviceEvent, address))
    }

    final def serviceAdded(serviceEvent: ServiceEvent) = filterSelfDiscovery(serviceEvent) { identifier =>
      fine("NODE_ADDED", identifier.name)
    }

    final def serviceResolved(serviceEvent: ServiceEvent) = filterSelfDiscovery(serviceEvent) { identifier =>
      fine("NODE_RESOLVED", identifier.name)

      fire(NodeDiscoveredEvent(ZeroconfListener.this, identifier))
    }

    final def serviceRemoved(serviceEvent: ServiceEvent) = filterSelfDiscovery(serviceEvent) { identifier =>
      fine("NODE_REMOVED", identifier.name)

      fire(NodeUndiscoveredEvent(ZeroconfListener.this, identifier))
    }

  }

  override def doStart = zeroconfs.foreach { zeroconf =>
    logging(classOf[Throwable], "CALLBACK_REGISTRATION_FAILED", serviceListener, zeroconf) {
      zeroconf.addServiceListener(serviceType, serviceListener)
    }
  }

  override def doStop = zeroconfs.foreach{ zeroconf =>
    logging(classOf[Throwable], "CALLBACK_UNREGISTRATION_FAILED", serviceListener, zeroconf) {
      zeroconf.removeServiceListener(serviceType, serviceListener)
    }
  }

  protected def createNodeIdentifier(serviceEvent: ServiceEvent, address: InetAddress) = Identifier(serviceEvent.getName, address.getHostAddress, serviceEvent.getInfo.getPort)

  override def toString = getClass.getSimpleName+" (%s)".format(zeroconfs.map { _.getName.toString }.mkString(","))

}