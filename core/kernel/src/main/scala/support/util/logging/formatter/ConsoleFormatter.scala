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
package support
package util
package logging
package formatter

import collection._
import console._
import lang.{Classes, Exceptions}

import java.text.{FieldPosition, MessageFormat, SimpleDateFormat}
import java.util.{Date, MissingResourceException, TimeZone}
import java.util.logging.{Formatter, Level, LogRecord, LogManager}

/** Basic [[Formatter]] using ANSI escape codes.
 *  Use a single line per [[LogRecord]].
 *
 *  @see [[ConsoleString]]
 */
class ConsoleFormatter extends Formatter {

  protected val format = new MessageFormat("\r{4} - ["+"{2}".escaped("{1}")+"] ["+"{0}".escaped(Console.UNDERLINED)+"] - {8}")
  protected val methodFormat = new MessageFormat("                 [Thread#{5}] {6}#{7}")
  private final val dateFormat = new SimpleDateFormat()
  private final val DefaultTimezone = TimeZone.getTimeZone("UTC")
  protected val timezone = DefaultTimezone
  private final val DontCareFieldPosition = new FieldPosition(0)

  protected final val PropertyPrefix = getClass.getName+"."
  /** @return property value as defined in `logging.properties`
   *  @see [[LogManager#getProperty]]
   */
  protected final def property(name: String) = Option(LogManager.getLogManager.getProperty(PropertyPrefix+name))
  protected final def propertyAsBoolean(name: String) = property(name) match {
    case Some(value) => value.toBoolean
    case None => false
  }

  protected final val PrintMethodDetailsPropertyName = "PrintMethodDetails"
  protected final def printMethodDetails = propertyAsBoolean(PrintMethodDetailsPropertyName)

  protected final val PrintExceptionDetailsPropertyName = "PrintExceptionDetails"
  protected final def printExceptionDetails = propertyAsBoolean(PrintExceptionDetailsPropertyName)

  protected final val UseCompactClassNamePropertyName = "UseCompactClassName"
  protected final def useCompactClassName = propertyAsBoolean(UseCompactClassNamePropertyName)

  protected final val ReverseStackTracePropertyName = "ReverseStackTrace"
  protected final def reverseStackTrace = propertyAsBoolean(ReverseStackTracePropertyName)

  protected val filteredClassNamePrefixesDelimiter = ";"
  protected final val FilteredClassNamePrefixesPropertyName = "FilteredClassNamePrefixes"
  protected final def filteredClassNamePrefixes: Array[String] = property(FilteredClassNamePrefixesPropertyName) match {
    case Some(value) => value.split(filteredClassNamePrefixesDelimiter)
    case None => Array()
  }

  protected final val MaximumMessageLengthPropertyName = "MaximumMessageLength"
  protected final def maximumMessageLength = property(MaximumMessageLengthPropertyName) match {
    case Some(value) => try {
      Some(value.toInt)
    } catch {
      case e: NumberFormatException => throw new IllegalArgumentException(MaximumMessageLengthPropertyName+" must be an integer")
    }
    case None => None
  }

  override def format(record: LogRecord) = {
    val parameters = Array(
      record.getLoggerName(),
      levelStyle(record.getLevel()),
      levelName(record.getLevel()),
      record.getSequenceNumber(),
      date(record.getMillis()),
      record.getThreadID(),
      record.getSourceClassName(),
      record.getSourceMethodName(),
      shortenedMessage(record))

    val writer = new StringBuilder()
    writer.append(format.format(parameters))
    if (record.getThrown() != null) {
      if (printExceptionDetails) writer.append(exceptionDetails(record.getThrown))
      else writer.append(" - "+exceptionSummary(record.getThrown))
    }
    if (printMethodDetails) writer.append('\n'+methodFormat.format(parameters))
    writer.append('\n')
    writer.toString
  }

  /** @return bold name if className starts with [[Root#packageName]]
   */
  private[this] final def prettifyClassName(name: String) = if (!name.startsWith(Root.packageName)) name else {
    name.substring(0, Root.packageName.length).escaped(Console.BOLD)+name.substring(Root.packageName.length)
  }

  /** @return [[Classes#compactName]] if `useCompactClassName` otherwise name
   */
  private[this] final def className(name: String) = {
    val finalName = if (useCompactClassName) Classes.compactName(name) else name
    prettifyClassName(finalName)
  }

  /** @return [[Throwable]] summary: `className`: `localizedMessage`
   */
  protected def exceptionHighlight(throwable: Throwable) = className(throwable.getClass.getName)+": "+throwable.getLocalizedMessage

  protected final def stack(throwable: Throwable) = {
    val stack = throwable +: Exceptions.causes(throwable)
    if (reverseStackTrace) stack.reverse else stack
  }

  protected final def exceptionSummary(throwable: Throwable) = {
    val separator = if (reverseStackTrace) "; from: " else "; caused by "
    val elements = stack(throwable)
    elements.toString("", separator)(element => exceptionHighlight(elements.head))
  }

  /** @return [[StackTraceElement]] summary: `className` (`fileName`:`lineNumber`)
   */
  protected final def stackTraceElementSummary(element: StackTraceElement) = className(element.getClassName)+"("+element.getFileName+":"+element.getLineNumber+")"

  /** @return non-filtered [[StackTraceElement]]s of specified [[StackTraceElement]]
   */
  protected final def stackTraceElements(throwable: Throwable) = throwable.getStackTrace.filterNot(element => filteredClassNamePrefixes.exists(element.getClassName().startsWith))

  /** Creates a String representation of a [[Throwable]].
   *  [[StackTraceElement]] whose [[Class#getName]] starts with one of `FilteredClassNamePrefixes` will be filtered.
   *  [[StackTraceElement]] whose [[Class#getName]] starts with [[Root#packageName]] will be in bold.
   */
  protected final def exceptionDetails(throwable: Throwable) = {
    def fillExceptionDetails(throwable: Throwable, builder: StringBuilder) = stackTraceElements(throwable).foreach(element => builder.append("  "+stackTraceElementSummary(element)+'\n'))
    val elements = stack(throwable)
    val head = elements.head
    val builder = new StringBuilder("\nStackTrace: "+exceptionHighlight(head)+'\n')
    fillExceptionDetails(head, builder)
    elements.tail.foreach { cause =>
      builder.append(if (reverseStackTrace) "From: " else "Caused by: ").append(exceptionHighlight(cause)+'\n')
      fillExceptionDetails(cause, builder)
    }
    builder.toString.dropRight(1)//Remove trailing \n
  }

  /** .@return ANSI style for specified [[Level]]
   */
  protected def levelStyle(level: Level): String = {
    def atLeastWarning(level: Level) = level.intValue >= Level.WARNING.intValue
    def atMostFine(level: Level) = level.intValue <= Level.FINE.intValue

    if (atLeastWarning(level)) {
      Console.RED
    } else if (atMostFine(level)) {
      Console.GREEN
    } else {
      Console.BLUE
    }
  }

  /** @return the maximum length of used {@see Level#getLocalizedName}
   */
  protected val longestLevelName = List(Level.OFF, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO, Level.WARNING, Level.SEVERE, Level.ALL).max(new Ordering[Level] {
    def compare(x: Level, y: Level) = x.getLocalizedName.lengthCompare(y.getLocalizedName.length)
  }).getLocalizedName.length

  /** @return {@link Level#getLocalizedName} left justified with as {@link #getLongestLevelName} as length
   */
  protected final def levelName(level: Level) = ("%-"+longestLevelName+"s").format(level.getLocalizedName)

  protected final def message(record: LogRecord): String = {
    val message = record.getMessage()
    val resource = Option(record.getResourceBundle()) match {
      //If a bundle exists and defines a mapping for this message (used as a key) use it.
      case Some(bundle) if bundle.containsKey(message) => bundle.getString(message)
      case Some(bundle) => Console.err.println("Failed to load resource <"+message+"> using <"+bundle+">"); message
      case None => message
    }

    def isResource(resource: String) = resource.contains("{")
    Option(record.getParameters()) match {
      case Some(parameters) if !parameters.isEmpty && isResource(resource) => try {
        MessageFormat.format(resource, parameters :_*)
      } catch {
        case _ => {
          Console.err.println("Failed to parse <"+resource+"> with <"+parameters+">")

          resource
        }
      }
      case _ => resource
    }
  }

  protected final def shortenedMessage(record: LogRecord) = {
    val fullMessage = message(record)
    maximumMessageLength match {
      case Some(value) if fullMessage.length() > value => fullMessage.substring(0, value)+"..."
      case _ => fullMessage
    }
  }

  /** @return formated timestamp as a UTC [[Date]].
   */
  protected final def date(millis: Long) = {
    val buffer = new StringBuffer(20)
    dateFormat.setTimeZone(timezone)
    dateFormat.format(new Date(millis), buffer, DontCareFieldPosition)//Safe as method is called by a single thread
    buffer.toString
  }

}