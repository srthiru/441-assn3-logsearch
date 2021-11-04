package rest.actor

import akka.actor.Actor
import rest.actor.LogFetcher.Get
import rest.data.Log

object LogFetcher{
  case object Get
}

class LogFetcher extends Actor{

  override def receive: Receive = {
    case Get => sender() ! Log("hmmm")
  }
}
