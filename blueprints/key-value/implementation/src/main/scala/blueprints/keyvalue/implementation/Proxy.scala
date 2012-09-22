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
package blueprints
package keyvalue
package implementation

import api.KeyValueStore
import cluster.View
import cluster.node.Identifier
import cluster.partitioning.Distributor
import cluster.partitioning.sharding.ShardingPartitioner
import cluster.routing.sequential.SequentialRouter
import hash.HashCodeObjectHasher
import serialization.Serializer
import transport.{Dispatcher, Message, Reply, Response}
import transport.protocol.Command
import uuid.{UUID, Generator}

import java.util.concurrent.Future

/** [[KeyValueStore]] proxy with [String] keys and [Array[Byte]] values.
 */
class Proxy(dispatchers: Identifier => Dispatcher[Command, Response], view: View, generator: Generator) extends KeyValueStore {

  val partitioner = new ShardingPartitioner[Command, Array[Byte]](new HashCodeObjectHasher(), { command: Command =>
    command match {
      case KeyExtractor(key) => key
      case _ => throw new IllegalArgumentException("Cannot extract key from <%0>".format(command))
    }
  })
  val router = new SequentialRouter[Command, Response]()
  val distributor = new Distributor(partitioner, router, dispatchers)

  def get(key: Array[Byte]) = distributor(GetCommand(generator(), key), view.reachableNodes).asInstanceOf[Reply[_]].result.asInstanceOf[Option[Array[Byte]]]

  def getAndRemove(key: Array[Byte]) = distributor(GetAndRemoveCommand(generator(), key), view.reachableNodes).asInstanceOf[Reply[_]].result.asInstanceOf[Option[Array[Byte]]]

  def put(key: Array[Byte], value: Array[Byte]) = distributor(PutCommand(generator(), key, value), view.reachableNodes)

  def remove(key: Array[Byte]) = distributor(RemoveCommand(generator(), key), view.reachableNodes)

}