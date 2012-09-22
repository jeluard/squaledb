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
package cluster
package partitioning
package sharding

import hash.ObjectHasher
import node.Identifier
import transport.Message

/** [[Partitioner]] implementation based on index inferred from [[Message]].
 *  Target node is chosen using following logic: nodes[index % nodes.size].
 */
class ShardingPartitioner[M <: Message, T](hasher: ObjectHasher[T], navigator: M => T) extends Partitioner[M] {

  final def apply(message: M, nodes: Set[Identifier]): Seq[Identifier] = if (nodes.isEmpty) Seq.empty else {
    val index = math.abs(hasher(navigator(message)))
    val modulo = index % nodes.size
    Stream.concat(nodes.drop(modulo), nodes.take(modulo))
  }

}

class IdentityShardingPartitioner[M <: Message, T](hasher: ObjectHasher[M]) extends ShardingPartitioner[M, M](hasher, {message: M => message})