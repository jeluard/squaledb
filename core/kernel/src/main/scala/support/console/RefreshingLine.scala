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
package support
package console

import measure.Frequency
import util.concurrent._

import java.io.{PrintStream, PrintWriter}
import java.util.concurrent.{Executors, ScheduledFuture, TimeUnit}

case class Update(message: String)
case object Stop

object RefreshingLine {

  private val executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("RefreshingLineThread"))

}

class RefreshingLine(frequency: Frequency = Frequency(1), out: PrintWriter = new PrintWriter(new PrintStream(System.out, false, "UTF-8"))) {

  private var message: Option[String] = None
  private var future: ScheduledFuture[_] = _

  def doStart = {
    future = RefreshingLine.executor.scheduleAtFixedRate(new Runnable() {
      def run = {
        message = update
        print
      }
    }, 0, frequency.value, TimeUnit.SECONDS)
  }

  def update: Option[String] = None

  def print = {
    out print "\r"
    message.map(out print)
    out flush
  }

  def doStop = {
    future.cancel(true)
  }

}