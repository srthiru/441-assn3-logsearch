import com.typesafe.config.ConfigFactory
import io.grpc.ManagedChannelBuilder
import org.slf4j.LoggerFactory
import rpcservice.protos.searcher.{SearchRequest, SearcherGrpc}
import rpcservice.protos.searcher.SearcherGrpc.{Searcher, SearcherBlockingStub, SearcherStub}

import scala.concurrent.Future

object SearchClient {

  val serverConf = ConfigFactory.load().getConfig("server")
  val logger = LoggerFactory.getLogger(this.getClass)

  val host = serverConf.getString("host")
  val port = serverConf.getInt("port")

  def main(args: Array[String]): Unit = {
//    val channelBuilder = ManagedChannelBuilder.forAddress(host, port)
//    channelBuilder.usePlaintext()
//    val channel = channelBuilder.build()
    val request = SearchRequest("Yo get this")

//    logger.info("Connecting to channel: " + channel.getState(true))

//    val asyncClient: SearcherBlockingStub = SearcherGrpc.blockingStub(channel)
//    val response = asyncClient.searchLog(request)

//    logger.info("Received response!! - " + Future.successful(response))
//    System.out.println("Received response!! - " + Future.successful(response))


  }
}
