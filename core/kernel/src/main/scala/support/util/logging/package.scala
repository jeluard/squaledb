/*
 * Copyright 2010 julien.
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
 * under the License.
 */

package org.squaledb
package support
package util

import java.io.InputStream
import java.util.logging.LogManager

package object logging {

  private[this] final val DefaultConfigurationLocation = "configuration/logging.properties"

  /** Read specified configuration.
   */
  final def readLoggingConfiguration(stream: InputStream) = LogManager.getLogManager.readConfiguration(stream)

  /** Read default configuration.
   */
  final def readDefaultLoggingConfiguration() {
    val classLoader = Thread.currentThread.getContextClassLoader
    val configurationStream = classLoader.getResourceAsStream(DefaultConfigurationLocation)

    require(configurationStream != null, "Failed to locate <"+DefaultConfigurationLocation+"> using <"+classLoader+">")

    readLoggingConfiguration(configurationStream)
  }

}