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
package serialization
package hessian2

import support.test.Specification

class Hessian2SerializerSpecification extends Specification with ByteArraySerializerBehaviors {

    val createdSerializer = new Hessian2Serializer()
    
    "A HessianSerializer" should {
        behave like serializer(new Hessian2Serializer[String])
    }

}
