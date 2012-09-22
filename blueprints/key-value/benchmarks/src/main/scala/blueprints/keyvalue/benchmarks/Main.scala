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
package benchmarks

import api.KeyValueStore
import base.benchmarks.Base
import cluster.View
import cluster.availability.perfect.PerfectFailureDetector
import cluster.zookeeper.ZooKeeperManager
import cluster.monitoring.Pinger
import cluster.node.{DispatcherCache, Identifier}
import cluster.partitioning.DistributionException
import implementation._
import measure.Time
import metric.Sample
import serialization.Serializer
import serialization.custom.ByteArrayCommandSerializer
import serialization.custom.ByteArrayResponseSerializer
import serialization.custom.VerbSerializer
import support.console._
import support.control.Exceptions._
import support.lang.Exceptions
import support.util.concurrent._
import support.util.logging.Logging
import transport.{Message, Response}
import transport.protocol.{Command, Verb}
import transport.netty.InstrumentedNettyDispatcher
import uuid.eaio.EAIOGenerator

import scala.util.control.Exception._

import org.clapper.argot._

import java.io.File
import java.util.concurrent.{ExecutionException, TimeUnit}

object Main extends Base("benchmarks") {

  import ArgotConverters._

  //Command line parameters/options
  val cluster = parser.parameter[String]("cluster", "Cluster name", false)

  val keyRangeOption = parser.option[Int](List("k", "key"), "n", "key range")
  val DefaultKeyRange = 1000
  lazy val keyRange = keyRangeOption.value.getOrElse(DefaultKeyRange)

  val payloadSizeOption = parser.option[Int](List("p", "payload"), "n", "payload size (bytes)")
  val DefaultPayloadSize = 10
  lazy val payloadSize = payloadSizeOption.value.getOrElse(DefaultPayloadSize)
  lazy val payload = scala.util.Random.nextString(payloadSize).getBytes

  lazy val manager = new ZooKeeperManager(clusterIdentifier=cluster.value.get)
  lazy val failureDetector = new PerfectFailureDetector()
  lazy val pinger = new Pinger(manager, failureDetector)
  lazy val view = new View(manager, failureDetector)
  lazy val cache = new DispatcherCache[Command, Response]({identifier: Identifier => new InstrumentedNettyDispatcher[Command, Response](identifier, new ByteArrayCommandSerializer(new VerbSerializer()), new ByteArrayResponseSerializer())}, failureDetector)
  lazy val store = new Proxy(cache, view, new EAIOGenerator())

  //TODO
  //Operations ratio get/put/remove
  //Display latency, throughput
  //Max, Min, Median, Mean, 99th, 99.9th percentile
  //val concurrent = parser.option[Int](List("c", "concurrent"), "n", "number of concurrent threads")

  def generateKey() = Array(scala.util.Random.nextInt(keyRange).toByte)

  def executeOperation(store: KeyValueStore) = {
    val key = generateKey()
    store.put(key, payload)
  }

  class WarmupThread(store: KeyValueStore, duration: Int) extends Thread {
    override def run = ignoring(classOf[InterruptedException], classOf[DistributionException]) {
      val before = System.currentTimeMillis
      while (System.currentTimeMillis-before < (duration*1000)) {
        executeOperation(store)
      }
    }
  }

  def createWarmupThread(duration: Int) = new WarmupThread(store, duration)

  class BenchmarkThread(store: KeyValueStore) extends Thread {
    override def run = ignoring(classOf[InterruptedException]) {
      val before = System.currentTimeMillis
      try {
        while (!Thread.currentThread.isInterrupted) {
          val before = System.currentTimeMillis
          val key = scala.util.Random.nextInt(keyRange)
          logging(classOf[DistributionException], "EXCEPTION") {
            try {
              val beforeExecution = System.currentTimeMillis
              executeOperation(store)
              reportExecution(System.currentTimeMillis-beforeExecution)
            } catch {
              case e: DistributionException => restoring { 
                Thread.sleep(5000)
                throw e
              }
            }
          }
        }
      } finally {
        printReport(Time(System.currentTimeMillis-before))
        cache.dispose()
      }
    }
  }

  def createBenchmarkThread() = new BenchmarkThread(store)

  val count = new java.util.concurrent.atomic.AtomicLong(0)
  val reportLine = new support.console.RefreshingLine() {
    var previous = 0L
    var previousDelta = 0L
    var currentDelta = 0L
    override def update = {
      val current = count.get
      currentDelta = current - previous
      val value = Option("["+currentDelta+"msg/s] "+trend(previousDelta, currentDelta)+" ("+current.toString+" msg sent)")
      previous = current
      previousDelta = currentDelta
      value
    }
  }

  val ratio = 0.15
  def trend(previous: Long, current: Long) = {
    if (current > previous*1+ratio) "\u21d7".escaped(Console.GREEN)
    else if (current < previous*1-ratio) "\u21d8".escaped(Console.RED)
    else "="
  }

  def reportExecution(duration: Long) = {
    count.incrementAndGet
  }

  def printReport(duration: Time) = {
    info("EXECUTION", String.format("%d", count.get.asInstanceOf[AnyRef]), duration, String.format("%d", (count.get/duration.asSeconds).asInstanceOf[AnyRef]))
  }

  override def benchmark(blockingCommand: => Unit) = {
    reportLine.doStart

    super.benchmark(blockingCommand)
  }

  override def execute = {
      cache.initialize
      cache.start
      view.initialize
      view.start

      pinger.initialize
      pinger.start

    super.execute
  }

}