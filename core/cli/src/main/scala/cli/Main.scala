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
package cli

import measure.{Frequency, Percentage}
import support.console._
import support.control.Exceptions._
import support.lang.Exceptions
import support.management._
import support.util.logging._

import java.io.{IOException, PrintWriter}

import javax.management.{MBeanAttributeInfo, MBeanNotificationInfo, MBeanOperationInfo, Notification}
import javax.management.remote.{JMXConnectionNotification, JMXConnector,JMXConnectorFactory, JMXServiceURL}

import scala.tools.nsc.{InterpreterLoop, Settings}
import scala.tools.nsc.interpreter._

/** [[InterpreterLoop]] with basic JMX commands pre-registered.
 */
class JMXInterpreterLoop(connector: JMXConnector) extends ILoop(None, new PrintWriter(Console.out)) with Logging {

  import scala.collection.JavaConversions._

  protected def echo(message: String) {
    out.println(message)
    out.flush
  }

  val connectionListener = (notification: Notification, handback: Option[AnyRef]) => notification.getType match {
    //TODO add reconnect mechanism
    case JMXConnectionNotification.FAILED => warning("CONNECTION_FAILED", None)
    case JMXConnectionNotification.CLOSED => warning("CONNECTION_CLOSED", None)
    case JMXConnectionNotification.NOTIFS_LOST => warning("CONNECTION_NOTIFICATIONS_LOST", None)
    case _ =>
  }
  connector.addConnectionNotificationListener(connectionListener, null, null)

  implicit val mBeanServerConnection = connector.getMBeanServerConnection

  /**
   * JMX commands.
   */

  def domainsCommand() = mBeanServerConnection.getDomains.foreach{ echo }

  def mbeansCommand() = mBeanServerConnection.queryNames(null, null).foreach{ name =>
    echo(name.toString)
  }

  def mBeanInfo(mBeanName: String) = mBeanServerConnection.getMBeanInfo(mBeanName)

  def echoAttributeInfo(attribute: MBeanAttributeInfo) = echo(attribute.getName)

  /** Print all attributes of specified [[MBeanInfo]]
   *  @see echoAttribute
   */
  def attributesCommand(mBeanName: String) = mBeanInfo(mBeanName).getAttributes.foreach{ echoAttributeInfo }

  /** Print value of specified attribute
   */
  def attributeValueCommand(line: String) = {
    val parameters = words(line)
    echo(mBeanServerConnection.getAttribute(parameters(0), parameters(1)).toString)
  }

  def echoOperationInfo(operation: MBeanOperationInfo) = echo(operation.getName)

  /** Print all operations of specified [[MBeanInfo]]
   *  @see echoOperation
   */
  def operationsCommand(mBeanName: String) = mBeanInfo(mBeanName).getOperations.foreach{ echoOperationInfo }

  def invokeOperationCommand(line: String) = {
    val parameters = words(line)
    //TODO
    //mBeanServerConnection.invoke(parameters(0), parameters(1), x$3, x$4)
    //
  }

  def echoNotification(notification: MBeanNotificationInfo) = notification.getNotifTypes.foreach{ notificationType =>
    echo(notificationType)
  }

  /** Print all notifications of specified [[MBeanInfo]]
   *  @see echoNotification
   */
  def notificationsCommand(mBeanName: String) = mBeanInfo(mBeanName).getNotifications.foreach{ echoNotification }

  /**
   */
  def registerNotificationCommand(line: String) = {
    def failMsg = "Argument must be the name of a method with signature (=> Notification, AnyRef): Unit"
    //val intp = ILoop.this.intp
    //val g: intp.global.type = intp.global
    //import g._

    words(line) match {
      case wrapper :: Nil => intp.typeOfExpression(wrapper) match {
        //case Some(PolyType(List(targ), MethodType(List(arg), restpe))) =>
        //  intp setExecutionWrapper intp.pathToTerm(wrapper)
        //  "Set wrapper to '" + wrapper + "'"
        case Some(x) => failMsg + "\nFound: " + x + intp.pathToTerm(wrapper)
        case _ => failMsg + "\nFound: <unknown>"
      }
      case _ => failMsg
    }
  }

  /**
   */
  def unregisterNotificationCommand(line: String) = {
    
  }

  /**
   * SqualeDB specific commands.
   */

  lazy val discoveryAggregatorMBean = ManagementFactory.discoveryAggregatorMBean
  lazy val clusterMembershipManagerMBean = ManagementFactory.clusterMembershipManagerMBean

  def printElements(elements: Iterable[AnyRef], label: String) {
    elements.map{ element => echo(element.toString) }
    echo(elements.size+" "+label+(if (elements.size > 1) "s" else ""))
  }

  //status (network, CPU, uptime with trends)
  def statusCommand() = {
    val progress = new ProgressBar
    val status = new RefreshingLine(Frequency(1), out) {
      var i = 0
      override def update = {
        //TODO mem via MemoryMXBean
        i = i+1
        progress.update(Percentage(i))
        Option("-- %s".format(i).escaped(Console.RED)+" "+progress.toString)
      }
    }
    status.doStart

    Console.in.readLine

    status.doStop
    ()
  }

  def discoveredNodesCommand() = printElements(discoveryAggregatorMBean.getNodes, "node")

  def discoveredNodeTargetsCommand(name: String) = printElements(discoveryAggregatorMBean.getNodeTargets(name), "target")

  def registerNodeCommand(parameters: List[String]) = {

  }

  //cluster (per node: OK, req/s avg, latency, mem usage)

  def quitCommand() = {
    logging(classOf[IOException], "CONNECTION_CLOSE_FAILED") {
      connector.removeConnectionNotificationListener(connectionListener)
      connector.close
    }

    Result(false, None)
  }

  override def printWelcome = {
    val message =
     """|Welcome to %s JMX console.
        |Type :help for more information.""" .stripMargin.format(Root.projectName.capitalize)

    echo(message)
  }

  import LoopCommand.{ cmd, nullary }

  override lazy val standardCommands = List(
     cmd("help", "[command]", "print this summary or command-specific help", helpCommand),
     historyCommand,
     cmd("h?", "<string>", "search the history", searchHistory),
     nullary("quit", "exit the interpreter", quitCommand)
  )

  lazy val customCommands = List(
    nullary("jmx-domains", "all domains", domainsCommand),
    nullary("jmx-mbeans", "all MBeans name", mbeansCommand),
    cmd("jmx-mbean-attributes", "<name>", "attributes  available on specified MBean", attributesCommand),
    cmd("jmx-mbean-attribute-value", "<name>", "value of a specific attribute of an MBean", attributeValueCommand),
    cmd("jmx-mbean-operations", "<name>", "operations  available on specified MBean", operationsCommand),
    //VarArgs("operation-invoke", "invoke an operation on an MBean", operationInvokeCommand),
    cmd("jmx-mbean-notifications", "<name>", "notifications available on specified MBean", notificationsCommand),
    cmd("jmx-mbean-register-notification", "<name> <notification> [function]", "register a notification listener", registerNotificationCommand),
    cmd("jmx-mbean-unregister-notification", "<name> <notification>", "unregister a notification listener", unregisterNotificationCommand),
    nullary("status", "this node status", statusCommand),
    nullary("discovered-nodes", "all discovered nodes", discoveredNodesCommand)//,
    //cmd("discovered-node-targets", "<name>", "all discovered targets", discoveredNodeTargetsCommand)
  )

  override def commands = customCommands ::: standardCommands

  override def command(line: String): Result = {
    val result = logging(classOf[Exception], "EXCEPTION_THROWN") {
      super.command(line)
    }
    if (result.isDefined) result.get else ()
  }

}

/** A simple JMX console based on standard Scala REPL.
 *  Allows to interact with a remote node.
 */
object Main {

  readDefaultLoggingConfiguration
  Exceptions.installDefaultUncaughtExceptionHandler

  import org.clapper.argot._
  import ArgotConverters._

  val parser = new ArgotParser("cli", true, preUsage = Some("Version 1.0"))
  val host = parser.parameter[String]("host", "JMX remote host.", false)
  val port = parser.parameter[Int]("port", "JMX remote port.", false)

  def main(args: Array[String]): Unit = {
    try {
      parser.parse(args)

      run
    } catch {
      case e: ArgotUsageException => println(e.message)
    }
  }

  def run = {
    val url = new JMXServiceURL(MBeanServerConnections.platformMBeanServerURL(host.value.get, port.value.get))
    val connector = JMXConnectorFactory.connect(url)
    val interpreter = new JMXInterpreterLoop(connector)
    val settings = new Settings(Console.println)
    settings.usejavacp.value = true
    interpreter.process(settings)
  }

}