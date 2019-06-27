package chatapp

import java.time.Instant

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import chatapp.UserAction.StartConversation

case class User(id : String, name : String)

//case class Conversation(sender : User, message: Message, receiver : Receiver )

case class Receiver()

case class Message( messageText :String , sentTime : Instant ) {
    // do the pre processing of the content
}

object UserAction {
  sealed trait UserActionRequest
  case class StartConversation(from :User, to : User) extends  UserActionRequest

  sealed trait SendMessage


}
class UserAction extends Actor with ActorLogging{

  override def receive: Receive = {
    case StartConversation(from, to) =>{
      println(s" user from $from to user $to")
      // initia a child actor as conversation

    }
    case _ => {println("Unknown user Action")}
  }

}
class Conversation extends Actor with ActorLogging{

  override def receive: Receive = {
    case _ => {println("started converstaion")}


  }

}


object application extends App{

  val application =ActorSystem("application")
  val actor =application.actorOf(Props[UserAction], "appuser")
  println(actor)

  actor!StartConversation(User("1", "saurabh"), User("2", "Narayana"))
  actor!StartConversation(User("1", "saurabh"), User("3", "Sathish"))

  val actorRef= application.actorSelection("/application/appuser")
  actorRef!StartConversation(User("1", "saurabh"), User("3", "Sathish"))
}





