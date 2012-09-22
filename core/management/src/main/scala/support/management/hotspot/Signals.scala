package org.squaledb
package support
package management
package hotspot

import sun.misc.{Signal, SignalHandler}

/** Helper methods for [[Signal]].
 *  @see http://www.ibm.com/developerworks/java/library/i-signalhandling/
 */
object Signals {

  /** Install a hook that will be called each time specified `signal` is raised.
   */
  final def install(signal: String, hook: => Unit) = {
    Signal.handle(new Signal(signal), new SignalHandler() {
      override def handle(raised: Signal) = hook
    })
  }

  /** Raise a `signal` in local VM.
   */
  final def raise(signal: String) = Signal.raise(new Signal(signal))

}
