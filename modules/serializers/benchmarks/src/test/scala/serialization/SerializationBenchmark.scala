/**
 * Copyright (C) 2010 julien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.squaledb
package serialization

import java.JavaSerializer
import hessian2.Hessian2Serializer
import sbinary.SBinaryStringSerializer
import msgpack.MessagePackStringSerializer

import com.carrotsearch.junitbenchmarks.{AbstractBenchmark, BenchmarkOptions}
import com.carrotsearch.junitbenchmarks.annotation.{AxisRange, BenchmarkHistoryChart, BenchmarkMethodChart, LabelType}

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

@AxisRange(min = 0)
@BenchmarkHistoryChart(labelWith = LabelType.CUSTOM_KEY, maxRuns = 20)
@BenchmarkMethodChart(filePrefix="method")
class SerializerBenchmark extends AbstractBenchmark with AssertionsForJUnit {

  val payload = {
    val size = 5000
    val buffer = new StringBuilder(size)
    for (i <- 0 to size) {
      buffer.append(scala.util.Random.nextInt)
    }
    buffer.toString
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
  def java = bench(new JavaSerializer)

  @Test
  @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
  def hessian2 = bench(new Hessian2Serializer)

  @Test
  @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
  def sbinary = bench(new SBinaryStringSerializer)

  @Test
  @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
  def messagepack = bench(new MessagePackStringSerializer)

  def bench(serializer: Serializer[String]): Unit = serializer.serialize(payload)

}