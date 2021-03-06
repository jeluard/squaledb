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

class Execution {

    val successes = new AtomicLong
    val failures = new AtomicLong

    def incrementSuccesses() = {
        successes.incrementAndGet();
    }

    def incrementSuccesses(occurences: Int) = {
        successes.addAndGet(occurences)
    }

    def getSuccesses() = {
        successes.get
    }

    def incrementFailures() = {
        failures.incrementAndGet
    }

    def incrementFailures(occurences: Int) = {
        failures.addAndGet(occurences)
    }

    def getFailures() = {
        failures.get
    }

}

