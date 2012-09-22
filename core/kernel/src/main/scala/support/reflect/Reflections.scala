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
package reflect

/**
 * Inspired by http://www.familie-kneissl.org/Members/martin/blog/reflection-from-scala-heaven-and-hell
 */
object Reflections {

    implicit def anyMaybeDo[T: Manifest](x: T) = new {
        def doIfInstanceOf[U: Manifest](body: U => Unit): Unit = {
            if (manifest[T] <:< manifest[U]) {
                body(x.asInstanceOf[U])
            }
        }
    }

    implicit def string2Class[T<:AnyRef](name: String)(implicit classLoader: ClassLoader): Class[T] = {
        val clazz = Class.forName(name, true, classLoader)
        clazz.asInstanceOf[Class[T]]
    }

    def instantiate[T](clazz: Class[T], arguments: WithType*): T = {
        arguments.size match {
              case 0 => clazz.newInstance
              case _ => {
                  val argumentTypes = arguments map { _.clazz } toArray
                  val candidates = clazz.getConstructors filter { constructor => matchingTypes(constructor.getParameterTypes, argumentTypes)}
                  require(candidates.length == 1, "Argument runtime types must select exactly one constructor")
                  val parameters = arguments map { _.value }
                  candidates.head.newInstance(parameters: _*).asInstanceOf[T]
              }
          }
    }

    /*def invoke[U](clazz: Class[T], method: String, arguments: WithType*): U = {
        clazz.getMethods 
    }
  
    def methods(clazz: Class[T]) = {
        
    }
  
    def properties(clazz: Class[T]) = {
      
    }*/
  
    private def matchingTypes(declared: Array[Class[_]], actual: Array[Class[_]]): Boolean = {
        declared.length == actual.length && (
          (declared zip actual) forall {
              case (declared, actual) => declared.isAssignableFrom(actual)
          })
    }

    implicit def refWithType[T<:AnyRef](x:T) = RefWithType(x, x.getClass)
    implicit def valWithType[T<:AnyVal](x:T) = ValWithType(x, getType(x))  
  
    sealed abstract class WithType {
      val clazz : Class[_]
      val value : AnyRef
    }

    case class ValWithType(anyVal: AnyVal, clazz: Class[_]) extends WithType {
      lazy val value = toAnyRef(anyVal)
    }

    case class RefWithType(anyRef: AnyRef, clazz: Class[_]) extends WithType {
      val value = anyRef
    }  

    def getType(x: AnyVal): Class[_] = x match {
      case _: Byte => java.lang.Byte.TYPE
      case _: Short => java.lang.Short.TYPE
      case _: Int => java.lang.Integer.TYPE
      case _: Long => java.lang.Long.TYPE
      case _: Float => java.lang.Float.TYPE
      case _: Double => java.lang.Double.TYPE
      case _: Char => java.lang.Character.TYPE
      case _: Boolean => java.lang.Boolean.TYPE
      case _: Unit => java.lang.Void.TYPE
    }

    def toAnyRef(x: AnyVal): AnyRef = x match {
      case x: Byte => Byte.box(x)
      case x: Short => Short.box(x)
      case x: Int => Int.box(x)
      case x: Long => Long.box(x)
      case x: Float => Float.box(x)
      case x: Double => Double.box(x)
      case x: Char => Char.box(x)
      case x: Boolean => Boolean.box(x)
      case x: Unit => ()
    }
  
    def methods(clazz: Class[_]) = clazz.getMethods.map { _.getName }.toSet
  
}
