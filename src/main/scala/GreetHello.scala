import akka.actor.{Actor, ActorSystem, Props}



class GreetHello extends App {


}

object GreetStart extends App{
  val system=ActorSystem("base-system")
  val greetActor =system.actorOf(Props[Greeter], "Greeter")

  val greetActor1 =system.actorOf(Props[Greeter], "Greeter1")
  val greetActor2 =system.actorOf(Props[Greeter], "Greeter2")
  greetActor ! GreetMessage("saurabh")
  greetActor1 ! GreetMessage("saurabh")
  greetActor2 ! GreetMessage("saurabh")
  greetActor ! new Object
  greetActor ! new GreetMessageMutable("saurabh")
}

case class GreetMessage(who : String)

class GreetMessageMutable(var who :String){

}
class Greeter extends Actor{

   def receive ={

    case GreetMessage(who) => println(s"Hello $who");
    case any => println(any.toString);
    case _ => println("default meth")
  }
}
