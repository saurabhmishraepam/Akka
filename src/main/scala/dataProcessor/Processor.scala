package dataProcessor

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import dataProcessor.Feeder.{AddDataRequest, DeleteDataRequest}
import dataProcessor.RequestValidator._
import akka.pattern.ask
import akka.util.Timeout

class Processor {

}

case class Data(id : String, content : String, owner : String)
case class User(userId : String)

object Feeder {
  sealed trait FeederRequest
  case class AddDataRequest(user : User, data : Data) extends FeederRequest
  case class DeleteDataRequest(user: User, data : Data) extends FeederRequest

  def props(requestValidatorRef: ActorRef, storageRef : ActorRef) : Props ={

    Props (classOf[Feeder], requestValidatorRef, storageRef)
  }
}
object  RequestValidator {
  sealed trait ValidationRequest
  case class ValidationRequestData(user : User, data : Data) extends ValidationRequest
  case class ValidUser(user: User) extends  ValidationRequest
  case class ValidData(data : Data) extends ValidationRequest
  case class AuthorizationValidation(user: User, data : Data) extends ValidationRequest


  // response
  sealed trait ValidationResponse
  case class WhiteUser(user : User) extends ValidationResponse
  case class BlackUser(user : User) extends ValidationResponse
  case class ValidRequest(user: User, data : Data) extends ValidationResponse
  case class InvalidRequest(user: User, data : Data) extends ValidationResponse

  def props : Props={
  Props[RequestValidator]
  }
}
object Storage{
  sealed trait StorageRequest
  case class AddRequest(user : User, data :Data) extends StorageRequest
  case class DeleteRequest(user : User, data : Data) extends StorageRequest
  case class UpdateRequest(user : User, data : Data) extends StorageRequest
  def props : Props ={
    Props [Storage]
  }
}

class Feeder(requestValidatorRef: ActorRef, storageRef : ActorRef) extends Actor with ActorLogging{
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit  val timeout =Timeout(2, TimeUnit.SECONDS)
  override def receive: Receive = {

    case AddDataRequest(user, data) =>{

      println(s"input data is $user, $data")

      requestValidatorRef ? ValidUser(user) map{
        case WhiteUser(user) => {

          println("Returned white user")

          requestValidatorRef ? AuthorizationValidation(user , data) map {

            case ValidRequest(user, data) => {
              // wht next is to store the data
              storageRef ! Storage.AddRequest(user, data)
              println("data getting stored ")

            }
            case _ =>{println("DefaultCase in validation of authorization")}
          }

        }
        case BlackUser(user) => {
          println("Returned Black user")
        }
        case _ =>{
          println("invoked" +user +data)
        }

      }
    }
    case DeleteDataRequest(user, data) =>{println(s"input data is $user, $data")}
    case _=>{println("Bad Request handling")}


  }

}

class RequestValidator extends Actor {
  val validUserList : List[User] =List(User("1"), User("2"), User("3"), User("4"))

  val dataStore : List[Data] =List()
  override def receive: Receive = {

    case ValidUser(user)  if(validUserList.contains(user)) =>{

      println(s"Validating user $user")
     sender()! WhiteUser(user)
    }
    case ValidUser(user) if(!validUserList.contains(user)) =>{
    sender() !  BlackUser(user)
    }
    case AuthorizationValidation(user, data) => {
        sender()! ValidRequest(user, data)
    }
    case _ =>{println("default case handling ")}
  }
}

class Storage extends Actor{

  var dataStoreLocal =List()
  override def receive: Receive = {
    case Storage.AddRequest(user, data) => {
      data :: dataStoreLocal
      println(s"data stored in the store $data  size: ")
    //  dataStoreLocal.foreach(d =>{print(d)})
    }
    case _ =>{println("default case handling ")}


  }

}

object Application extends App{

  val system = ActorSystem("rootsystem")

  val requestValidatorRef = system.actorOf(RequestValidator.props, "requestValidator")
  val storageActorRef =system.actorOf(Storage.props, "storageActor")

  val feederRef = system.actorOf(Feeder.props(requestValidatorRef, storageActorRef), "feeder")

  feederRef ! AddDataRequest(User("1"), Data("1", "This is the first Content", "1"))
  feederRef ! AddDataRequest(User("1"), Data("2", "This is the first Content", "1"))
  feederRef ! AddDataRequest(User("1"), Data("3", "This is the first Content", "1"))
  feederRef ! AddDataRequest(User("9"), Data("1", "This is the first Content" ,"9"))

}














