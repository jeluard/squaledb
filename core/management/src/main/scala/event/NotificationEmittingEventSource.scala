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
package event

import support.management.{AttributeChangeNotificationEmitting, MBeanLifeCycle}

import javax.management.{MBeanServer, ObjectName}

trait NotificationEmittingEventSource[T <: Event] extends MBeanLifeCycle with AttributeChangeNotificationEmitting { self: EventProducer[T] =>

  val listener: T => Unit = event => sendNotification(event)

  protected def sendNotification(event: T): Unit = {
    sendNotification("event", event.source, "Event sent")
  }

  abstract override def preRegister(server: MBeanServer, name: ObjectName) = {
    addListener(listener)

    super.preRegister(server, name)
  }

  abstract override def preDeregister = removeListener(listener)

}
