package chatapp


import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import chatapp.Parent.{RestartException, ResumeException, StopException}

class ParentChildControls {

}
case class msg(content : String)

object Child{

  def props = {
    Props[Child]
  }
}

class Child extends Actor{

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("actor restarted ")
  }

  override def postStop(): Unit = {
    println("actor before stop")
  }

  override def receive: Receive = {
    case "restart" =>{println(msg)
    throw RestartException
    }
    case "resume" =>{
      throw ResumeException
    }
    case "stop" => {
      throw StopException
    }
    case _ => {println("default handler")}
  }
}
object Parent{
  case object ResumeException extends Exception
  case object RestartException extends Exception
  case object StopException extends Exception


}
class Parent extends Actor{

  var childRef : ActorRef =_

  override def preStart(): Unit = {
    childRef=context.actorOf(Child.props, "childactor")
    Thread.sleep(100)
  }
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    childRef=context.actorOf(Child.props, "childactor")
    Thread.sleep(2000)
  }

  override def receive: Receive = {

    case msg =>{
      childRef!msg
    }

  }

override val supervisorStrategy =OneForOneStrategy(maxNrOfRetries = 10){
  case ResumeException =>{Resume}
  case RestartException =>{Restart}
  case StopException =>{Stop}
  case _ : Exception => Escalate
}


}

object AppRunnerSupervision extends App{
  val system =ActorSystem("system")

  val parentActorRef=system.actorOf(Props[Parent], "parent")

  parentActorRef!"restart"
  parentActorRef!"resume"
  parentActorRef!"stop"


}

