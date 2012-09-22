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
package management

import util.logging.Logging

import javax.management.ObjectName

/** Helper to register/unregister MBean.
 */
object JMX extends Logging {

  final val Domain = Root.packageName
  final val NameExtractor = """(.*)\.(.*)""".r

  final def extractName(any: AnyRef): ObjectName = {
    val name = any.getClass.getName
    val beanName = any.getClass.getName match {
      case NameExtractor(decoration, name) => name
      case _ => name
    }
    Domain+":name="+beanName
  }

}