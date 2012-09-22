package org.squaledb
package support
package util

import java.util.{Enumeration, ResourceBundle}

import scala.collection.JavaConverters._

/** [ResourceBundle] composite backed by a [List] of [ResourceBundle]. Uses first one defined needed `key`.
 */
class CompositeResourceBundle(bundles: List[ResourceBundle]) extends ResourceBundle {

  override final def handleGetObject(key: String) = bundles.find(bundle => bundle.containsKey(key)) match {
    case Some(bundle) => bundle.getObject(key)
    case None => null
  }

  override final def getKeys() = asJavaEnumerationConverter(bundles.flatMap(bundle => enumerationAsScalaIteratorConverter(bundle.getKeys()).asScala).iterator).asJavaEnumeration

}