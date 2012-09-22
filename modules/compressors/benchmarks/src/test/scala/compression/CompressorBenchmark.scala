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
package compression
package benchmarks

import lzf.LZFCompressor
import zip.ZIPCompressor

import com.carrotsearch.junitbenchmarks.{AbstractBenchmark, BenchmarkOptions}
import com.carrotsearch.junitbenchmarks.annotation.{AxisRange, BenchmarkHistoryChart, BenchmarkMethodChart, LabelType}

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

@AxisRange(min = 0)
@BenchmarkHistoryChart(labelWith = LabelType.CUSTOM_KEY, maxRuns = 20)
@BenchmarkMethodChart
class CompressorBenchmark extends AbstractBenchmark with AssertionsForJUnit {

  val bytes = {
    val size = 5000000
    val buffer = new StringBuilder(size)
    for (i <- 0 to size) {
      buffer.append(scala.util.Random.nextInt)
    }
    buffer.toString.getBytes
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
  def lzf = bench(new LZFCompressor)

  @Test
  @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
  def zip = bench(new ZIPCompressor)

  def bench(compressor: Compressor): Unit = compressor.compress(bytes)

}