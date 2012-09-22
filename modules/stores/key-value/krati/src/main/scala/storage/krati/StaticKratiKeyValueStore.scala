/*
 * Copyright 2011 julien.
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
 */

package org.squaledb
package storage
package krati

import java.io.File

import _root_.krati.core.segment.SegmentFactory
import _root_.krati.store.StaticDataStore
import _root_.krati.util.{FnvHashFunction, HashFunction}

/** [[KratiKeyValueStore]] implementation backed by a [[StaticDataStore]].
 */
class StaticKratiKeyValueStore(home: File, capacity: Int, entrySize: Int = 10000, maxEntries: Int = 5 , segmentFileSizeMB: Int = 256 , segmentFactory: SegmentFactory, segmentCompactFactor: Double = 0.5 , hashFunction: HashFunction[Array[Byte]] = new FnvHashFunction()) extends KratiKeyValueStore {

  val store = new StaticDataStore(home, capacity, entrySize, maxEntries, segmentFileSizeMB, segmentFactory, segmentCompactFactor, hashFunction)

}