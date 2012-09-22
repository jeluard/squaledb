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
package reflect

import org.squaledb.support.test.Specification
import org.mockito.Mockito._

class ReflectionsSpecification extends Specification {
  
    trait A {
      def test
    }
    
    trait B extends A
  
    "An Object with correct type" should {

        "have method be called" in {
            import support.reflect.Reflections._
            
            val any = mock[A]
            any.doIfInstanceOf[A]{any => any.test}
            
            verify(any).test 
        }
        
    }

    "An Object's parent with correct type" should {

        "have method be called" in {
            import support.reflect.Reflections._
            
            val any = mock[B]
            any.doIfInstanceOf[A]{any => any.test}
            
            verify(any).test
        }
        
    }
  
    "An Object with incorrect type" should {

        "have method not be called" in {
            import support.reflect.Reflections._
            
            val any = mock[A]
            any.doIfInstanceOf[B]{any => any.test}
            
            verify(any, never()).test
        }
        
    }
  
}
