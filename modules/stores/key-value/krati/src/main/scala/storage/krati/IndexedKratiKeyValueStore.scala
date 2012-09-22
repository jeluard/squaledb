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
import _root_.krati.store.IndexedDataStore

/** [[KratiKeyValueStore]] implementation backed by a [[IndexedDataStore]].
 */
class IndexedKratiKeyValueStore(home: File, batchSize: Int, numSyncBatches: Int, indexInitLevel: Int = 8 , indexSegmentFileSizeMB: Int = 32 , indexSegmentFactory: SegmentFactory, storeInitLevel: Int = 8 , storeSegmentFileSizeMB: Int = 256, storeSegmentFactory: SegmentFactory) extends KratiKeyValueStore {

  val store = new IndexedDataStore(home, batchSize, numSyncBatches, indexInitLevel, indexSegmentFileSizeMB, indexSegmentFactory, storeInitLevel, storeSegmentFileSizeMB, storeSegmentFactory)

}