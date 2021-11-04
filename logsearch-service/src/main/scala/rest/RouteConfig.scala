package rest

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.get
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.{PathDirectives, RouteDirectives}
import akka.pattern.Patterns
import com.typesafe.config.ConfigFactory
import rest.actor.LogFetcher
import rest.data.Log

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class RouteConfig(implicit val logDataRef: ActorRef, implicit val sys: ActorSystem) {

  val conf = ConfigFactory.load().getConfig("rest")

  val timeout = conf.getInt("timeout");

  val getRoute: Route =

    PathDirectives.pathPrefix("log"){
      get {
        val log = findLog(LogFetcher.Get)

        RouteDirectives.complete(HttpEntity(log.toString))
      }
    }


  def findLog(message: Any) = {
    val duration = Duration.create(timeout, TimeUnit.MILLISECONDS)
    val resultFuture = Patterns.ask(logDataRef, message, duration)
    val result = Await.result(resultFuture, duration).asInstanceOf[Log]
    result
  }

}