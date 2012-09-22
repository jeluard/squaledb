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
package serialization

import org.squaledb.support.test.Specification

trait SerializerBehaviors { this : Specification =>

    def serializer(serializer: Serializer[String]) = {

        "perform serialize/unserialize as noop" in {
            val value = "test"

            serializer.unserialize(serializer.serialize(value)) should equal (value)
        }

    }

}