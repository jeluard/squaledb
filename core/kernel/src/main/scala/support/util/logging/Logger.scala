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

import collection._
import lang.Classes
import util.CompositeResourceBundle

import scala.annotation.elidable
import scala.annotation.elidable._

import java.util.{Locale, MissingResourceException, ResourceBundle}
import java.util.logging.{Filter, Level, Logger => JULLogger, LogRecord}

object Logger {

  final val BundlePath = "logging/"
  final val BundleSuffix = "resources"

  /** Generate resource bundle name based on predefined convention. For packageName `com.organisation` it would be `com/organisation/resources`.
   *
   *  @return a resource bundle name extracted from current package name
   */
  final def resourceBundleName(pack: Package) = BundlePath + (if (Root.packageName.equals(pack.getName)) BundleSuffix else pack.getName.stripPrefix(Root.packageName+".").replaceAll("\\.", "/")+ "/"+BundleSuffix)

  /** Find [[ResourceBundle]] using [[Locale#getDefault]] and [[Thread#currentThread#getContextClassLoader]].
   */
  final def findResourceBundle(name: String): Option[ResourceBundle] = try {
    Option(ResourceBundle.getBundle(name, Locale.getDefault, Thread.currentThread.getContextClassLoader))
  } catch {
    case e: MissingResourceException => None
  }

}

/** Logger facility relying on [[java.util.logging.Logger]].
 *  Compile using '-Xelide-below `level`' to completely remove log calls bellow level.
 */
class Logger[T : Manifest](clazz: Class[_]) {

  def this() = this(manifest[T].erasure)

  protected val name = clazz.getPackage.getName
  private[this] final val packages = clazz.getPackage +: Classes.allSuperclasses(clazz).map(_.getPackage).filterNot(_ == classOf[java.lang.Object].getPackage).removeDuplicates
  private[this] final val bundles = packages.map(pack => Logger.findResourceBundle(Logger.resourceBundleName(pack))).flatten

  private[this] final val julLogger =  JULLogger.getLogger(name)

  private[this] final def createLogRecord(level: Level, message: String, parameters: Array[Any]) = {
    val logRecord = new LogRecord(level, message)
    logRecord.setLoggerName(julLogger.getName)
    logRecord.setParameters(parameters.asInstanceOf[Array[AnyRef]])
    logRecord.setResourceBundle(new CompositeResourceBundle(bundles) {
      override def toString() = packages.toString("Composite of <", ", ", ">")(_.getName())
    })
    logRecord
  }

  private[this] final def jullog(level: Level, message: => String, throwable: Option[Throwable], parameters: Array[Any]): Unit = if (julLogger.isLoggable(level)) {
    val logRecord = createLogRecord(level, message, parameters)
    throwable.map(logRecord.setThrown)
    jullog(logRecord)
  }

  private[this] final def jullog(logRecord: LogRecord) = julLogger.log(logRecord)

  /** @see [[JULLogger#setFilter(Filter)]]
   */
  final def setFilter(filter: Filter) = julLogger.setFilter(filter)

  //Message is not passed-by-name as it is always used as a resource bundle key.

  /** Won't be elided.
   */
  @inline
  final def log(level: Level, message: String, parameters: Any*) = jullog(level, message, None, parameters.toArray)
  @inline
  final def log(level: Level, message: String, t: Throwable, parameters: Any*) = jullog(level, message, Option(t), parameters.toArray)

  @inline
  @elidable(FINEST)
  final def finest(message: String, parameters: Any*) = jullog(Level.FINEST, message, None, parameters.toArray)
  @inline
  @elidable(FINEST)
  final def finest(message: String, t: Throwable, parameters: Any*) = jullog(Level.FINEST, message, Option(t), parameters.toArray)

  @inline
  @elidable(FINER)
  final def finer(message: String, parameters: Any*) = jullog(Level.FINER, message, None, parameters.toArray)
  @inline
  @elidable(FINER)
  final def finer(message: String, t: Throwable, parameters: Any*) = jullog(Level.FINER, message, Option(t), parameters.toArray)

  @inline
  @elidable(FINE)
  final def fine(message: String, parameters: Any*) = jullog(Level.FINE, message, None, parameters.toArray)
  @inline
  @elidable(FINE)
  final def fine(message: String, t: Throwable, parameters: Any*) = jullog(Level.FINE, message, Option(t), parameters.toArray)

  @inline
  @elidable(INFO)
  final def info(message: String, parameters: Any*) = jullog(Level.INFO, message, None, parameters.toArray)
  @inline
  @elidable(INFO)
  final def info(message: String, t: Throwable, parameters: Any*) = jullog(Level.INFO, message, Option(t), parameters.toArray)

  @elidable(CONFIG)
  final def config(message: String, parameters: Any*) = jullog(Level.CONFIG, message, None, parameters.toArray)
  @elidable(CONFIG)
  final def config(message: String, t: Throwable, parameters: Any*) = jullog(Level.CONFIG, message, Option(t), parameters.toArray)

  @inline
  @elidable(WARNING)
  final def warning(message: String, parameters: Any*) = jullog(Level.WARNING, message, None, parameters.toArray)
  @inline
  @elidable(WARNING)
  final def warning(message: String, t: Throwable, parameters: Any*) = jullog(Level.WARNING, message, Option(t), parameters.toArray)

  @inline
  @elidable(SEVERE)
  final def severe(message: String, parameters: Any*) = jullog(Level.SEVERE, message, None, parameters.toArray)
  @inline
  @elidable(SEVERE)
  final def severe(message: String, t: Throwable, parameters: Any*) = jullog(Level.SEVERE, message, Option(t), parameters.toArray)

}