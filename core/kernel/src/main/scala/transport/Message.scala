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
package transport

import uuid.UUID

/** Base class for message passing transport.
 */
class Message(val uuid: UUID)

sealed abstract class Response(override val uuid: UUID) extends Message(uuid)

/** [[Response]] used for request/reply type exchange.
 */ 
case class Reply[T](override val uuid: UUID, val result: T) extends Response(uuid)

/** [[Response]] used as dummy reply.
 */
case class Ack(override val uuid: UUID) extends Response(uuid)