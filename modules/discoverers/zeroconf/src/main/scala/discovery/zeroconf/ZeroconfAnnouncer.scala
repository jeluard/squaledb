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
import support.util.logging.Logging

import javax.jmdns.ServiceInfo

/** Zeroconf based [[Announcer]].
 *
 * JmDNS apparently breaks if two process on the machine advertise same info (name and port).
 */
class ZeroconfAnnouncer extends Announcer with ZeroconfProvider with Logging {

  import support.control.Exceptions._
  
  val serviceInfo = createServiceInfo(Node.identifier)

  protected final def createServiceInfo(identifier: Identifier) = {
    import scala.collection.JavaConversions._

    ServiceInfo.create(serviceType, identifier.name, identifier.port, 0, 0, extractMetadata(identifier))
  }  
  
  protected def extractMetadata(identifier: Identifier): Map[String, String] = {
    Map()//TODO add timestamp to make sure each restart forces serviceResolved
  }

  override def doStart = {
    zeroconfs.foreach{ zeroconf => 
      logging(classOf[Throwable], "REGISTRATION_FAILED", zeroconf) {
        zeroconf.registerService(serviceInfo) 
      }
    }                  

    fine("ANNOUNCEMENT_STARTED", serviceInfo.getName, serviceInfo.getType)
  }

  override def doStop = {
    zeroconfs.foreach{ zeroconf => 
      logging(classOf[Throwable], "UNREGISTRATION_FAILED", zeroconf) {
        zeroconf.unregisterService(serviceInfo)
      }
    }

    fine("ANNOUNCEMENT_STOPPED", serviceInfo.getName, serviceInfo.getType)
  }
  
  override def toString = getClass.getSimpleName+" (%s)".format(zeroconfs.map { _.getName.toString }.mkString(","))

}