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

package org.squaledb.support.management;

import java.io.Serializable;

public class Aggregate<T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    public long count = 0;
    public T min;
    public T max;
    public T last;

    public synchronized void add(final T last) {
        this.count++;
        this.last = last;
        if (this.min == null || last.compareTo(this.min) < 0) {
            this.min = last;
        }
        if (this.max == null || last.compareTo(this.max) > 0) {
            this.max = last;
        }
    }

    public long getCount() {
        return this.count;
    }

    public T getLast() {
        return this.last;
    }

    public T getMin() {
        return this.min;
    }

    public T getMax() {
        return this.max;
    }

}

