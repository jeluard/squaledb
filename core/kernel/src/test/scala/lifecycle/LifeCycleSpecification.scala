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
package lifecycle

import support.test.Specification

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

class LifeCycleSpecification extends Specification {

    class DummyLifeCycle(val blocking: Boolean = false) extends LifeCycle {

        val startCalled = new AtomicInteger
        val stopCalled = new AtomicInteger
        val startCallLatch = new CountDownLatch(1)
        val startBlockingLatch = new CountDownLatch(1)
        val stopCallLatch = new CountDownLatch(1)
        val stopBlockingLatch = new CountDownLatch(1)

        def awaitStartBlocking = startBlockingLatch.await
        def awaitStopBlocking = stopBlockingLatch.await
        def unblockStart = startCallLatch.countDown
        def unblockStop = stopCallLatch.countDown

        initialize

        override def doStart = {
            if (blocking) {
                startBlockingLatch.countDown
                startCallLatch.await
            }

            startCalled.incrementAndGet
        }

        override def doStop = {
            if (blocking) {
                stopBlockingLatch.countDown
                stopCallLatch.await
            }

            stopCalled.incrementAndGet
        }

    }

    "A LifeCycle" should {

        "be initialized" in {
            new DummyLifeCycle().state should equal (Initialized)
        }

        "be startable" in {
            val lifeCycle = new DummyLifeCycle(true)
            val thread = new Thread(new Runnable() {
                def run = {
                    lifeCycle.start
                }
            })
            thread.start

            lifeCycle.awaitStartBlocking
            lifeCycle.state should equal (Starting)
            lifeCycle.unblockStart
            thread.join
            lifeCycle.state should equal (Started)
            lifeCycle.startCalled.get should equal (1)
        }

        "not be stoppable" in {
            val lifeCycle = new DummyLifeCycle();

            intercept[IllegalStateException] {
                lifeCycle.stop
            }
        }

        "not be startable by concurrrent threads" in {
            val lifeCycle = new DummyLifeCycle(true)
            val thread1 = new Thread(new Runnable() {
                def run = lifeCycle.start
            })
            val thread2 = new Thread(new Runnable() {
                def run = lifeCycle.start
            })
            thread1.start
            thread2.start
            lifeCycle.unblockStart
            thread1.join
            thread2.join

            lifeCycle.startCalled.get should equal (1)
        }

        "be stoppable" in {
            val lifeCycle = new DummyLifeCycle(true)
            lifeCycle.unblockStart
            lifeCycle.start
            val thread = new Thread(new Runnable() {
                def run = {
                    lifeCycle.stop
                }
            })
            thread.start

            lifeCycle.awaitStopBlocking
            lifeCycle.state should equal (Stopping)
            lifeCycle.unblockStop
            thread.join
            lifeCycle.state should equal (Stopped)
            lifeCycle.stopCalled.get should equal (1)
        }

        "not be startable" in {
            val lifeCycle = new DummyLifeCycle()
            lifeCycle.start

            intercept[IllegalStateException] {
                lifeCycle.start
            }
        }

        "not be stoppable by concurrrent threads" in {
            val lifeCycle = new DummyLifeCycle(true)
            lifeCycle.unblockStart
            lifeCycle.start
            val thread1 = new Thread(new Runnable() {
                def run = lifeCycle.stop
            })
            val thread2 = new Thread(new Runnable() {
                def run = lifeCycle.stop
            })
            thread1.start
            thread2.start
            lifeCycle.unblockStop
            thread1.join
            thread2.join

            lifeCycle.stopCalled.get should equal (1)
        }
    }

}
