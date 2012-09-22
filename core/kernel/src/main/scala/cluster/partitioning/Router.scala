/**
 * Copyright (C) 2010 julien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.squaledb
package cluster
package partitioning

import transport.{Dispatcher, Message, Response}

/** [[Exception]] thrown when [[Router]] failed to [[Router#route]] a [[Message]].
 */
class RoutingException(message: String) extends Exception(message)

/** Abstract logic used to create a valid [[Exchange]] from [[Dispatcher]]s.
 *  @throws RoutingFailureException Message can't be routed'
 */
trait Router[I <: Message, O <: Response] extends Function2[I, Seq[Dispatcher[I, O]], O]