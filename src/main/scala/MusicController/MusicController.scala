package MusicController

import akka.actor.{Actor, ActorSystem, Props}


object MusicController{
  sealed trait ControllerMsg
  case object Play extends ControllerMsg
  case object Stop extends ControllerMsg
}

class MusicController extends Actor{
  def receive ={
    case MusicController.Play =>println("Starting Music Controller")
    case MusicController.Stop =>{println("I don't want to stop it")}
    case _ => println("Not ")
  }

}

object MusicPlayer{
    sealed trait PlayMsg
    case object StopMusic extends PlayMsg
    case object StartMusic extends PlayMsg
  def prop= Props[MusicPlayer]
}

class MusicPlayer extends Actor{

  def receive ={
    case MusicPlayer.StartMusic =>{println("start music command");}
    case MusicPlayer.StopMusic =>{println("sto music command")}
  }
}

object Runner {

  def main(args : Array[String ]): Unit = {
    val system = ActorSystem("system")
    val musicPlayerRef = system.actorOf(MusicPlayer.prop, "musicplayer")
    println(system)
    println(musicPlayerRef)
    musicPlayerRef ! MusicPlayer.StartMusic

    musicPlayerRef ! MusicPlayer.StopMusic

  }



}

