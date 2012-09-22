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
package event

import support.test.Specification

import java.util.concurrent.CountDownLatch

class EventProducerSpecification extends Specification {

  case class DummyEvent(producer: DummyEventProducer) extends Event(Option(producer))

  class DummyEventProducer extends EventProducer[DummyEvent] {
      def fireDummyEvent = fire(new DummyEvent(this))
  }

  "An EventProducer" when {

      "initializing" should {

          "allow adding one listener" in {
            val producer = new DummyEventProducer
            producer.addListener((event: DummyEvent) => ())
          }

          "allow adding several listeners" in {
            val producer = new DummyEventProducer
            producer.addListener((event: DummyEvent) => ())
            producer.addListener((event: DummyEvent) => ())
          }

          "allow removing existing listener" in {
            val producer = new DummyEventProducer
            val listener = (event: DummyEvent) => ()
            producer.addListener(listener)
            producer.removeListener(listener)
          }

          "prevent removal of non existing listener" in {
            val producer = new DummyEventProducer
            intercept[IllegalArgumentException] {
                producer.removeListener((event: DummyEvent) => ())
            }
          }

          "prevent adding of existing listener" in {
            val producer = new DummyEventProducer
            val listener = (event: DummyEvent) => ()
            producer.addListener(listener)
            intercept[IllegalArgumentException] {
                producer.addListener(listener)
            }
          }

      }

      "firing events" should {

          "propagate events to one listener" in {
            val producer = new DummyEventProducer
            val received = new CountDownLatch(1)
            producer.addListener((event: DummyEvent) => received.countDown)
            producer.fireDummyEvent
            received.await
          }

          "propagate events to several listeners" in {
            val producer = new DummyEventProducer
            val received = new CountDownLatch(2)
            producer.addListener((event: DummyEvent) => received.countDown)
            producer.addListener((event: DummyEvent) => received.countDown)
            producer.fireDummyEvent
            received.await
          }

          "ignore listener throwing exception" in {
           val producer = new DummyEventProducer
           val received = new CountDownLatch(1)
           producer.addListener((event: DummyEvent) => throw new RuntimeException)
           producer.addListener((event: DummyEvent) => received.countDown)
           producer.fireDummyEvent
           received.await
          }

      }

  }
}
