package dataProcessor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}


import scala.util.Random



class Worker extends Actor{


  override def receive: Receive = {

    case Worker.Work(msg) =>{
      println(s" This is the actor $self $msg")
    }

  }
}

object Worker {
  case class Work(msg : String)

}

class Routing extends Actor{
  var routees :List[ActorRef] =_

  override def preStart(): Unit = {

    routees =List.fill(5){
      context.actorOf(Props[dataProcessor.Worker])
    }


  }
  override def receive: Receive = {

    case Worker.Work(msg) =>{
      print("Mess"+msg)
      routees(util.Random.nextInt(routees.size)) forward(msg)

    }
    case _ =>{
      println("test ")
    }
  }
}


object Routing extends App {

val system = ActorSystem("systemapp")

  val router =system.actorOf(Props[Routing])

  List.fill(20){

    router!Worker.Work("Test "+util.Random.nextInt)
  }

}


