import com.typesafe.config.ConfigFactory
import io.grpc.ManagedChannelBuilder
import rpcservice.protos.searcher.{SearchRequest, SearcherGrpc}
import rpcservice.protos.searcher.SearcherGrpc.{Searcher, SearcherBlockingStub, SearcherStub}

import java.util.logging.Logger

object SearchClient {

  val serverConf = ConfigFactory.load().getConfig("server")
  val logger = Logger.getLogger(this.getClass.getName)

  val host = serverConf.getString("host")
  val port = serverConf.getInt("port")

  def main(args: Array[String]): Unit = {
    val channelBuilder = ManagedChannelBuilder.forAddress(host, port)
    channelBuilder.usePlaintext()
    val channel = channelBuilder.build()
    val request = SearchRequest(args(0), args(1))

    logger.info("Connecting to channel: " + channel.getState(true))

    val asyncClient: SearcherBlockingStub = SearcherGrpc.blockingStub(channel)
    val response = asyncClient.searchLog(request)

    logger.info("Received response!! - " + response.message)
  }
}
