/*
 *  Copyright 2010 julien.
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
package cluster
package zookeeper

import lifecycle.LifeCycle
import node.Identifier
import support.control.Exceptions._
import support.lang._
import support.util.logging.Logging

import java.util.concurrent.CountDownLatch
import org.apache.zookeeper.{CreateMode, WatchedEvent, Watcher, ZooDefs, ZooKeeper}

/** [[Manager]] implementation based on http://zookeeper.apache.org/.
 *
 * @see http://zookeeper.apache.org/doc/zookeeperProgrammers.html
 * @see http://wiki.apache.org/hadoop/ZooKeeper/FAQ
 * @see http://wiki.apache.org/hadoop/ZooKeeper/ErrorHandling
 * @see http://wiki.apache.org/hadoop/ZooKeeper/Troubleshooting
 */
class ZooKeeperManager(connectionString: String = "localhost:2181", clusterIdentifier: String) extends Manager with LifeCycle with Logging {

  support.util.logging.log4j.installRedirection()

  val connectionWatcher = new Watcher {
    import Watcher.Event.KeeperState._
    def process(event: WatchedEvent) = {
      event.getState match {
        case SyncConnected => {
          fine("CONNECTED", connectionString)

          connection.countDown
        }
        case Disconnected => warning("DISCONNECTED", connectionString)
        case AuthFailed => warning("AUTHENTICATION_FAILED", connectionString)
        case Expired => warning("EXPIRED", connectionString)
      }
    }
  }
  val sessionTimeout = 3000 //See http://zookeeper.apache.org/doc/zookeeperProgrammers.html#zookeeper_sessions
  lazy val zooKeeper = new ZooKeeper(connectionString, sessionTimeout, connectionWatcher)
  val connection = new CountDownLatch(1)

  val Delimiter = "/"
  val RootPath = Delimiter+Root.projectName
  val ClusterPath = RootPath+Delimiter+clusterIdentifier
  val AnyVersion = -1

  def pathForNodes(node: Identifier) = ClusterPath+Delimiter+node
  val HostPort = """(.*):(\d+)""".r

  protected final def createEphemeral(path: String) = zooKeeper.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)
  protected final def createPersistent(path: String) = zooKeeper.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
  protected final def delete(path: String) = zooKeeper.delete(path, AnyVersion)

  override def doInitialize = restoring {
    zooKeeper
    connection.await

    //Create Cluster path if non-existent. Will be persistent.
    if (zooKeeper.exists(RootPath, true) == null) {
      createPersistent(RootPath)
    }
    if (zooKeeper.exists(ClusterPath, false) == null) {
      createPersistent(ClusterPath)
    }
  }

  override def doClose = restoring {
    zooKeeper.close()
  }

  def nodes = if (zooKeeper.exists(ClusterPath, false) != null) {
    import scala.collection.JavaConversions._
    /*for (identifier <- zooKeeper.getChildren(ClusterPath, false)) identifier match {
      case HostPort(host, port) => yield Identifier(host, host, port.toInt)
      case _ => warning("UNRECOGNIZED_IDENTIFIER", None, identifier)
    }*/
    Set(zooKeeper.getChildren(ClusterPath, false) :_ *).flatMap { identifier: String =>
      identifier match {
        case HostPort(host, port) => { try {
          Some(Identifier(host, host, port.toInt))
        } catch {
          case _ => warning("INVALID_INDENTIFIER_PORT", identifier); None
        }}
        case _ => warning("UNRECOGNIZED_IDENTIFIER", identifier); None
      }
    }
  } else {
    Set[Identifier]()
  }

  def add(identifier: Identifier) = restoring {
    val path = pathForNodes(identifier)
    require(zooKeeper.exists(path, false) == null, "Node <%s> is already registered".format(identifier))

    createEphemeral(path)
  }

  def remove(identifier: Identifier) = restoring {
    val path = pathForNodes(identifier)
    require(zooKeeper.exists(path, false) != null, "Node <%s> is not registered".format(identifier))

    delete(path)
  }

}