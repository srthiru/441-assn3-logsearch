import com.typesafe.config.ConfigFactory
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http._
import com.twitter.util.{Await, Future}

import java.util.logging.Logger


object RestClient {

  val logger = Logger.getLogger(this.getClass.getName)
  val config = ConfigFactory.load()

  def main(args: Array[String]): Unit ={

    val host = config.getString("url")
    val port = config.getString("port")

    logger.info(s"Connecting to service at $host:$port")
    val client: Service[Request, Response] = Http.client
                                                  .newService(f"$host:$port")

    val request = Request(Method.Get, f"/findlogs?interval=${args(0)}&delta=${args(1)}")
//    val request = Request(Method.Get, "/hello")
    val response: Future[Response] = client(request)

    response.foreach(resp => print(s"Here is the response ${resp.contentString}"))
    Await.result(response)
  }
}
