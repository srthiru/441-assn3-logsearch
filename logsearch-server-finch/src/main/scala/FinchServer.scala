
import cats.effect.IO
import com.twitter.finagle.{Http, Service, Status}
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.logging.Logger
import com.twitter.util.{Await, Duration, Future}
import com.typesafe.config.ConfigFactory
import io.finch.Endpoint
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import io.circe.generic.auto._
import scalaj.http
import scalaj.http.HttpResponse

object FinchServer {

  val logger = Logger.get(this.getClass)
  val conf = ConfigFactory.load()

  // Case class to represent the input interval parameter that will be serialized
  case class Interval(
                   interval: String,
                   delta: Int,
                 )

  // Case class for the output message that will be serialized
  case class Log(log: String)

  def main(args: Array[String]): Unit = {
    val logPath = conf.getString("server.logMethod")

    // Test hello endpoint
    val sayHi: Endpoint[IO, String] = get("hello") {
      Ok("hello")
    }

    // Defining an endpoint with parameters that accepts an interval
    val findInterval: Endpoint[IO, Interval] = (param[String]("interval") :: param[Int]("delta")).as[Interval]

    // findlogs endpoint that invokes the lambda function
    val getLogs: Endpoint[IO, Response] = get(logPath :: findInterval) { interval: Interval =>

      logger.info(s"Received request: ${interval.interval}, ${interval.delta}")

      val result: HttpResponse[String] = http.Http(conf.getString("lambda.apiEndpoint")).param("interval", interval.interval).param("delta", interval.delta.toString).asString
//                  .option(http.HttpOptions.readTimeout(conf.getInt("lambda.timeout")))

      val response = Response()
      response.setContentString(result.body + result.headers + result.code)
      response
    }

    val port = conf.getString("server.port")
    logger.info(s"Starting server at $port")

    // Serve the API at the defined port - default 8081
    Await.ready(Http.server.serve(":"+port, (sayHi :+: getLogs).toServiceAs[Application.Json]))
  }

}
