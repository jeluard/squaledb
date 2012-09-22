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

import cluster.node.Identifier
import event.{Event, EventProducing}
import support.util.logging.Logging

import scala.actors.Actor
import scala.collection.JavaConversions._
import scala.collection.mutable.{ConcurrentMap, HashMap, MultiMap}

import java.util.concurrent.ConcurrentHashMap

sealed abstract class AdvertisementEvent(val identifier: Identifier) extends Event(None)
case class NodeAdvertisedEvent(override val identifier: Identifier) extends AdvertisementEvent(identifier)
case class NodeAdvertisementUpdatedEvent(override val identifier: Identifier) extends AdvertisementEvent(identifier)
case class NodeUnadvertisedEvent(override val identifier: Identifier) extends AdvertisementEvent(identifier)

/** Aggregates [[DiscoveryEvent]] from different [[Listener]]. Provides a unified view of all discovered [[Node]].
 */
class Aggregator extends Actor with Logging/*extends EventProducing[AdvertisementEvent]*/ {

  private val listeners: ConcurrentMap[Listener, DiscoveryEvent => Unit] = new ConcurrentHashMap[Listener, DiscoveryEvent => Unit]
  val discovered: MultiMap[String, Identifier] = new HashMap[String, collection.mutable.Set[Identifier]] with MultiMap[String, Identifier]

  //TODO change lifecycle so that it does not define start method
  start

  def register(listener: Listener) = {
    val eventListener: (DiscoveryEvent => Unit) = { event =>
      Aggregator.this ! event
    }
    listener.addListener(eventListener)
    listeners.update(listener, eventListener)
  }

  /** Stop listening to this [[Listener]]. [[DiscoveryEvent]] received from this [[Listener]] won't be invalidated.'
   */
  def unregister(listener: Listener) = {
    listeners.remove(listener) match {
      case Some(eventListener) => listener.removeListener(eventListener)
      case None =>
    }
  }

  def act() = {
    loop {
      react {
        case NodeDiscoveredEvent(source, nodeIdentifier) => nodeDiscovered(nodeIdentifier.name, nodeIdentifier)
        case NodeUndiscoveredEvent(source, nodeIdentifier) => nodeUndiscovered(nodeIdentifier.name, nodeIdentifier)
      }
    }
  }

  protected def nodeDiscovered(id: String, identifier: Identifier) = {
    val newNode = discovered.contains(id)

    discovered.addBinding(identifier.name, identifier)

    if (newNode) {
      finer("NODE_ADVERTISED", identifier.name, identifier)
    } else {
      finer("NODE_ADVERTISEMENT_CHANGED", identifier.name, discovered.get(id).get.mkString(","))
    }
  }

  protected def nodeUndiscovered(id: String, identifier: Identifier) = {
    if (discovered.contains(id)) {
      discovered.removeBinding(id, identifier)

      if (!discovered.contains(id)) {
        finer("NODE_UNADVERTISED", identifier.name)
      } else {
        finer("NODE_ADVERTISEMENT_CHANGED", identifier.name, discovered.get(id).get.mkString(","))
      }
    } else {
      finer("UNKNOWN_NODE_UNADVERTISED", identifier.name)
    }
  }

  def nodes = {
    discovered.keySet
  }

  def identifiers(name: String) = {
    discovered.get(name)
  }

}