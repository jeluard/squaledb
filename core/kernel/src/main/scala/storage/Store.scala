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
package storage

import lifecycle.LifeCycle

import java.io.Flushable

/** Base trait for stores.
 */
trait Store

/** Marker trait identifying persistent [[Store]].
 */
trait PersistentStore extends Store

/** Marker trait identifying [[PersistentStore]] having all operations disk-synchronized.
 */
trait SynchronousPersistentStore extends PersistentStore

/** [[PersistentStore]] supporting [[Flushable]] semantics.
 */
trait FlushablePersistentStore extends PersistentStore with Flushable

/** [[PersistentStore]] allowing to be repaired.
 */
trait RepairableStore extends PersistentStore {

  /** Attempt to repair underlying storage if needed.
   */
  def repair: Unit

}

/** [[PersistentStore]] allowing to be destroyed.
 */
trait DestroyableStore extends PersistentStore {

  /** Attempt to destroy underlying storage.
   */
  def destroy: Unit

}