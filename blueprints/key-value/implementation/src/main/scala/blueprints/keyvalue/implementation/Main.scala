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
package blueprints
package keyvalue
package implementation

import api.KeyValueStore
import base.implementation.Base
import cluster.membership.ManagerMXBean
import cluster.zookeeper.ZooKeeperManager
import cluster.node.Identifier
import serialization.Serializer
import serialization.custom.ByteArrayCommandSerializer
import serialization.custom.ByteArrayResponseSerializer
import serialization.custom.VerbSerializer
import storage.SynchronousPersistentKeyValueStore
import storage.{ByteArrayKeyValueStoreWrapper, CountAwareKeyValueStoreMXBean, InstrumentedCountAwareKeyValueStore, InstrumentedKeyValueStore, InstrumentedFlushablePersistentStore}
import storage.memory.MemoryKeyValueStore
import transport.{Ack, Message, Reply, Response}
import transport.protocol.{Command, Verb}
import transport.netty.InstrumentedNettyReceiver

import org.clapper.argot._

import java.util.concurrent.ConcurrentHashMap

object Main extends Base("node") {

  import ArgotConverters._

  val cluster = parser.parameter[String]("cluster", "Cluster name", false)

  def start {
    val manager = new ZooKeeperManager(clusterIdentifier=cluster.value.get) with ManagerMXBean

    manager.initialize

    val identifier = Identifier("localhost", "localhost", port.value.get)
    if (!manager.isRegistered(identifier)) {
      manager.register(identifier)
    }

    import collection.JavaConverters._
    val store = new ByteArrayKeyValueStoreWrapper(new MemoryKeyValueStore(new ConcurrentHashMap[Any, Array[Byte]]().asScala) with InstrumentedKeyValueStore[Any, Array[Byte]] with InstrumentedCountAwareKeyValueStore[Any, Array[Byte]] with CountAwareKeyValueStoreMXBean)

    val receiver = new InstrumentedNettyReceiver[Command, Response](port.value.get, new ByteArrayCommandSerializer(new VerbSerializer()), new ByteArrayResponseSerializer()) {
      def apply(command: Command) = command match {
        case GetParametersExtractor(key) => Reply(command.uuid, store.get(key))
        case GetAndRemoveParametersExtractor(key, _*) => synchronized {
          val value = store.get(key)
          store.remove(key)
          Reply(command.uuid, value)
        }
        case PutParametersExtractor(key, value) => {
          store.put(key, value)
          Ack(command.uuid)
        }
        case RemoveParametersExtractor(key, _*) => {
          store.remove(key)
          Ack(command.uuid)
        }
        case _ => throw new IllegalArgumentException("Malformed command <"+command+">")
      }
    }
    receiver.initialize
    receiver.start
  }

}