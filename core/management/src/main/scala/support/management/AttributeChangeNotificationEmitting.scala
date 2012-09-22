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

import javax.management.{AttributeChangeNotification, NotificationBroadcasterSupport, NotificationEmitter, NotificationFilter, NotificationListener}

/** [[NotificationEmitter]] implementation sending [[AttributeChangeNotification]].
 */
trait AttributeChangeNotificationEmitting extends NotificationEmitting {

  protected final def sendAttributeChangeNotification(source: AnyRef, oldValue: AnyRef, newValue: AnyRef, message: String, attributeName: String, attributeType: String) = {
    val notification = new AttributeChangeNotification(source, createSequenceNumber, createTimeStamp, message, attributeName, attributeType, oldValue, newValue)
    sendNotification(notification)
  }

}
