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
package support
package util
package logging
package log4j

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.spi.LoggingEvent

import java.util.logging.LogRecord

object JULAppender {
  val name = getClass.getSimpleName
}

/** Redirects log4j logs into a [[Logger]].
 */
class JULAppender extends AppenderSkeleton {

  setName(JULAppender.name)

  def asLevel(level: org.apache.log4j.Level) = level match {
    case org.apache.log4j.Level.ALL => java.util.logging.Level.ALL
    case org.apache.log4j.Level.FATAL => java.util.logging.Level.SEVERE
    case org.apache.log4j.Level.ERROR => java.util.logging.Level.SEVERE
    case org.apache.log4j.Level.WARN => java.util.logging.Level.WARNING
    case org.apache.log4j.Level.INFO => java.util.logging.Level.INFO
    case org.apache.log4j.Level.DEBUG => java.util.logging.Level.FINE
    case org.apache.log4j.Level.TRACE => java.util.logging.Level.FINEST
    case _ => java.util.logging.Level.parse(level.toString())
  }

  def asLogRecord(event: LoggingEvent) = {
    val logRecord = new LogRecord(asLevel(event.getLevel), event.getMessage.toString)
    logRecord.setLoggerName(event.getLoggerName)
    logRecord.setMillis(event.getTimeStamp)
    if (event.getThrowableInformation != null) {
      logRecord.setThrown(event.getThrowableInformation.getThrowable)
    }
    logRecord
  }

  override def append(event: LoggingEvent) = {
    val logger = java.util.logging.Logger.getLogger(event.getLoggerName)
    logger.log(asLogRecord(event))
  }

  override def requiresLayout = false

  override def close() = {}

}