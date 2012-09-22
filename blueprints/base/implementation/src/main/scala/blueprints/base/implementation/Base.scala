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
package blueprints
package base
package implementation

import support.lang.Exceptions
import support.management._
import support.util.logging._

import org.clapper.argot._

/** Base for store implementation.
 */
abstract class Base(name: String) extends Logging {

  import ArgotConverters._

  val parser = new ArgotParser(name, true, preUsage = Some("Version 1.0"))
  val port = parser.parameter[Int]("port", "Listening port.", false)
  val jmxPort = parser.option[Int](List("p", "jmxPort"), "n", "JMX port.")

  readDefaultLoggingConfiguration
  Exceptions.installDefaultUncaughtExceptionHandler

  def main(args: Array[String]): Unit = {
    try {
      parser.parse(args)

      run
    } catch {
      case e: ArgotUsageException => println(e.message)
    }
  }

  def run {
    jmxPort.value.map{ support.management.hotspot.MBeanServers.enableUnsecureRemoteMonitoring }

    start()

    info("STARTED")

    Thread.currentThread.join
  }

  def start()

}