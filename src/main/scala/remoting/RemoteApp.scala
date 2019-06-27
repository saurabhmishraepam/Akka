package remoting

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory


class RemoteApp {

}


class Worker extends Actor{

  override def receive: Receive = {

    case msg =>{println(s" This is the msg $msg")}

  }

}

object RemoteApp extends App{

  val config =ConfigFactory.load.getConfig("MembersService")
  println(config)

  val system =ActorSystem("MemberService", config)

  println(system)

  val workerRef= system.actorOf(Props[Worker], "worker1")

  workerRef!"testmessage"


}

object RemoteInvocation extends App{

val config =ConfigFactory.load.getConfig("MembersServiceLookup")
val system =ActorSystem("lookup", config)

  val workerRef = system.actorSelection("akka.tcp://MemberService@127.0.0.1:2552/user/worker1")

  workerRef!" testmessage remotely"

  workerRef!" testmessage remotely"
}



