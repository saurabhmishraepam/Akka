package chatapp

import akka.actor.{Actor, ActorSystem}
import chatapp.UserAction.StartConversation
import chatapp.application.actor

class UserController extends Actor{

  override def receive: Receive = {

    case _ =>{}

  }
}


object UserControllerRunner extends App{

  val application =ActorSystem("application")

val actorRef=application.actorSelection("useractivityhandler")
  println(actorRef)

  //actorRef ! "test me"
  actorRef!StartConversation(User("1", "saurabh"), User("2", "Narayana"))
}


