package rest

import akka.actor.{ActorSystem, Props}
import akka.actor.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import rest.actor.LogFetcher
import rest.repository.LogRepositoryImpl

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object WebServer extends App {

  val conf = ConfigFactory.load().getConfig("rest")

  implicit val sys: ActorSystem = ActorSystem("web-app")
  private implicit val dispatcher: ExecutionContextExecutor = sys.dispatcher
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val logRepo = new LogRepositoryImpl()
  implicit val logDataRef: ActorRef = sys.actorOf(Props(new LogFetcher))

  private val routeConf = new RouteConfig()
  val routes = {
    pathPrefix("api") {
      Directives.concat(
        routeConf.getRoute
      )
    }
  }

  val ip = conf.getString("ip")
  val port = conf.getInt("port")

  val serveFuture = Http().bindAndHandle(routes, ip, port)

  println("Server started")
  StdIn.readLine()
  serveFuture
    .flatMap(_.unbind())
    .onComplete(_ => sys.terminate())
}
