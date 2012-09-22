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
package management

import java.util.concurrent.atomic.AtomicLong

import javax.management.{Notification, NotificationBroadcasterSupport, NotificationEmitter, NotificationFilter, NotificationListener}

/** Basic [[NotificationEmitter]] implementation sending [[Notification]].
 */
trait NotificationEmitting extends NotificationEmitter {

  private val emitter = new NotificationBroadcasterSupport
  private val counter = new AtomicLong

  protected def createSequenceNumber = counter.incrementAndGet

  protected def createTimeStamp = System.currentTimeMillis

  protected final def sendNotification(notification: Notification): Unit = emitter.sendNotification(notification)

  protected final def sendNotification(notificationType: String, source: AnyRef, message: String): Unit = {
    val notification = new Notification(notificationType, source, createSequenceNumber, createTimeStamp, message)
    sendNotification(notification)
  }

  final def addNotificationListener(listener: NotificationListener,filter: NotificationFilter, handback: AnyRef) = emitter.addNotificationListener(listener, filter, handback)

  final def removeNotificationListener(listener: NotificationListener) = emitter.removeNotificationListener(listener)

  final def removeNotificationListener(listener: NotificationListener,filter: NotificationFilter, handback: AnyRef) = emitter.removeNotificationListener(listener, filter, handback)

  final def getNotificationInfo = emitter.getNotificationInfo

}
