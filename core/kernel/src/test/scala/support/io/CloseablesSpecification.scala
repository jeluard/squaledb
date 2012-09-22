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
package io

import java.io.{Closeable, IOException}
import java.util.concurrent.atomic.AtomicBoolean

import test.Specification

class CloseablesSpecification extends Specification {

    class DummyCloseable extends Closeable {
      
      val closeCalled = new AtomicBoolean
    
      def close = closeCalled.set(true)
    
    }
    
    class ThrowingIOExceptionCloseable extends Closeable {
      
      val closeCalled = new AtomicBoolean
    
      def close = {
          closeCalled.set(true)
          throw new IOException
      }
    
    }  
    
    class ThrowingOtherExceptionCloseable extends Closeable {
    
      def close = throw new RuntimeException
    
    } 
  
    "A Closeable" should {

        "be closed" in {
            val closeable = new DummyCloseable
            using(closeable) {closeable =>}
            
            closeable.closeCalled.get should equal (true)
        }
        
    }
    
    "A Closeable throwing IOException" should {

        "be closed and exception ignored" in {
            val closeable = new ThrowingIOExceptionCloseable
            using(closeable) {closeable =>}
            
            closeable.closeCalled.get should equal (true)
        }
        
    }
    
    "A Closeable throwing other Exceptions" should {

        "be closed and exception propagated" in {
            intercept[RuntimeException] {
                using(new ThrowingOtherExceptionCloseable) {closeable =>}
            }
        }
        
    }
  
}
