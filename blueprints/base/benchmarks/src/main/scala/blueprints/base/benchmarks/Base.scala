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
package base
package benchmarks

import measure.Time
import metric.Sample
import support.console._
import support.control.Exceptions._
import support.lang.Exceptions
import support.util.concurrent._
import support.util.logging._


import java.io.File
import java.util.concurrent.{ExecutionException, TimeUnit}

import org.clapper.argot._

/** Base for store benchmarks.
 */
abstract class Base(name: String) extends Logging {

  import ArgotConverters._

  //Command line parameters/options
  val parser = new ArgotParser(name, true, preUsage = Some("Version 1.0"))

  val warmupTimeOption = parser.option[Int](List("w", "warmup"), "n", "warmup time (seconds)")
  val DefaultWarmupTime = 5
  lazy val warmupTime = warmupTimeOption.value.getOrElse(DefaultWarmupTime)

  //If not specified long run (never stops)
  val durationOption = parser.option[Long](List("d", "duration"), "n", "duration (seconds)")

  readDefaultLoggingConfiguration
  Exceptions.installDefaultUncaughtExceptionHandler

  def main(args: Array[String]): Unit = {
    try {
      parser.parse(args)

      execute
    } catch {
      case e: ArgotUsageException => println(e.message)
    }
  }

  def warmup(duration: Int) {
    val thread = createWarmupThread(duration)
    thread.start
    thread.join
  }

  def createWarmupThread(duration: Int): Thread

  def benchmark(blockingCommand: => Unit) = {
    val thread = createBenchmarkThread()
    thread.start

    blockingCommand

    thread.interrupt
  }

  def createBenchmarkThread(): Thread

  /** Perform warmup then launch benchmark.
   */
  def execute = {
    info("WARMUP", warmupTime)

    warmup(warmupTime)

    info("WARMUP_DONE")

    if (durationOption.value.isDefined) {
      val duration = durationOption.value.get

      info("RUN", duration)

      benchmark(Thread.sleep(TimeUnit.SECONDS.toMillis(duration)))
    } else {
      info("LONG_RUN")

      benchmark(Console.in.read)
    }

    info("RUN_DONE")
  }

}