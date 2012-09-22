/*
 *  Copyright 2011 julien.
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
package metric

import measure.Percentage

object Decile {def apply(sample: Sample) = Percentile(sample, Percentage(10))}
object Quartile {def apply(sample: Sample) = Percentile(sample, Percentage(25))}
object Median {def apply(sample: Sample) = Percentile(sample, Percentage(50))}

case class Percentile(sample: Sample, value: Percentage) {

  override def toString = {
    ""
  }

}