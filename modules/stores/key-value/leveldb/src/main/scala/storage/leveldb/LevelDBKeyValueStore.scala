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
package krati

import lifecycle.LifeCycle

import org.fusesource.leveldbjni._
import org.fusesource.leveldbjni.DB._

import java.io.File

/** [[KeyValueStore]] implementation relying on LevelDB.
 *
 *  @see http://code.google.com/p/leveldb/
 *  @see https://github.com/fusesource/leveldbjni
 */
class LevelDBKeyValueStore(file: File) extends KeyValueStore[Array[Byte], Array[Byte]] with PersistentStore with RepairableStore with DestroyableStore with LifeCycle {

  val options = new Options()
  val readOptions = new ReadOptions()
  val writeOptions = new WriteOptions()
  val repairOptions = new Options()
  val destroyOptions = new Options()
  lazy val db = DB.open(options, file)

  override def doInitialize = options.createIfMissing(true)

  override def doStart = db

  override def doStop = db.delete()

  def get(key: Array[Byte]): Option[Array[Byte]] = Option(db.get(readOptions, key))

  def put(key: Array[Byte], value: Array[Byte]) = db.put(writeOptions, key, value)

  override def putAll(maps: Map[Array[Byte], Array[Byte]]) = {
    val batch = new WriteBatch()
    maps.foreach { entry => batch.put(entry._1, entry._2) }
    db.write(writeOptions, batch)
  }

  def remove(key: Array[Byte]) = db.delete(writeOptions, key)

  override def removeAll(keys: Seq[Array[Byte]]) = {
    val batch = new WriteBatch()
    keys.foreach { batch.delete }
    db.write(writeOptions, batch)
  }

  def repair() = DB.repair(file, repairOptions)

  def destroy() = DB.destroy(file, destroyOptions)

}