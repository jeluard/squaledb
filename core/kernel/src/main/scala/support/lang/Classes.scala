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
package support
package lang

/** Helper methods for [[Class]].
 */
object Classes {

  final val ToString = "toString"

  /** Return a compact representation of specified [[Class]] name.
   *  First 2 ad last segments are untouched while others are shorten to their first character.
   *
   *  @example {{{
   *    scala> compactName("aaa.bbb.ccc.ddd.eee")
   *    res1: String = aaa.bbb.c.d.eee
   *  }}}
   */
  final def compactName(name: String): String = {
    val segments = name.split('.')
    if (segments.length <= 3) {
      return name
    }
    val compactName = new StringBuilder
    compactName.append(segments(0)).append(".").append(segments(1)).append(".")
    segments.drop(2).dropRight(1).foreach{ segment =>
      compactName.append(segment(0)+".")
    }
    compactName.append(segments.last)
    compactName.toString
  }

  final def allSuperclasses(clazz: Class[_]) = {
    var superClasses = List[Class[_]]()
    var superClass = clazz.getSuperclass()
    while (superClass != null) {
      superClasses = superClass +: superClasses
      superClass = superClass.getSuperclass
    }
    superClasses
  }

  /** @return true if [[Class]] defines a `method`. [[Any]] default implementations are not considered.
   */
  final def definesMethod(method: String, clazz: Class[_]) = {
    val superClasses = allSuperclasses(clazz).diff(Seq(classOf[Any]))
    superClasses.exists(clazz => try {
      clazz.getDeclaredMethod(method)
      true
    } catch {
      case e => false
    })
  }

  /** @return true if [[#ToString]] is defined.
   *  @see [[#definesMethod(String, Any)]]
   */
  final def definesToString(clazz: Class[_]) = definesMethod(ToString, clazz)

  final def friendlyName(any: AnyRef) = {
    val clazz = any.getClass
    if (definesToString(clazz)) any.toString else clazz.getSimpleName
  }

}