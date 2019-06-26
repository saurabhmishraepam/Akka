package userRegistration

import java.util.concurrent.TimeUnit

import akka.actor
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import userRegistration.Checker.{BlackUser, CheckUser, WhiteUser}
import userRegistration.Recorder.AddUser
import userRegistration.Storage.StoreUser
import akka.pattern.ask
import akka.util.Timeout
class UserRegistration {

}
case class User(name :String, email :String)

object Checker{
  sealed trait CheckerMessage
  case class CheckUser(user :User) extends CheckerMessage

  sealed trait ResponseMessage
  case class WhiteUser(user: User) extends ResponseMessage
  case class BlackUser(user: User) extends ResponseMessage
}

object Recorder{
  sealed trait RecorderMessage
  case class AddUser(user : User) extends RecorderMessage
  def props(checkerRef : ActorRef, storageref: ActorRef): Props ={

    Props(classOf[Recorder], checkerRef, storageref)

  }
}

object Storage{
  sealed trait StoreMessage
  case class StoreUser(user: User) extends StoreMessage
}

class Recorder(checkerRef : ActorRef, sotrageRef : ActorRef) extends Actor{
import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout=Timeout(2, TimeUnit.MICROSECONDS )
  override def receive: Receive = {


    case AddUser(user)=>{
      println("Invoked")
      checkerRef ? CheckUser(user) map {

        case WhiteUser(user) => {
          println(s"this is the ivokation $user")
          sotrageRef ! StoreUser(user)
        }
        case BlackUser(user) => {println("bad user registration failed")}
        case _ =>{println("default invocation")}
      }
    }

  }
}

class Checker extends Actor{
  var blackListUser= List(User("saurabh","saurabh_mishra@epam.com"))
  override def receive: Receive = {

    case CheckUser(user)
      if(blackListUser.contains(user)) => {
      sender() ! BlackUser(user)}
    case CheckUser(user)
      => {
      println("this is the call")
      sender() !  WhiteUser(user)}
  }
}

class Storage extends Actor{

  var storage =List()
  override def receive: Receive = {
    case StoreUser(user) =>{
      println("added to the list $user");
      storage :+user
      storage.foreach(us=>{print(us)})

    }
    case _ =>{println("this is the invokation of default")}
  }
}

object Runner extends App{

  val system =ActorSystem("registration")

  val checkerRef=system.actorOf(Props[Checker], "checker")
  val storageRef=system.actorOf(Props[Storage], "storgae")

  val recorderRef= system.actorOf(Recorder.props(checkerRef, storageRef), "syetem")

  recorderRef!AddUser(User("saurabh", "saurabh_mishra@epam.com"))
  recorderRef!AddUser(User("rohit", "rohit@epam.com"))

  recorderRef!AddUser(User("saurabh1", "saurabh_mishra@epam.com"))

}





