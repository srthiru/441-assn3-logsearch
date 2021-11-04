package rpcservice

import com.typesafe.config.ConfigFactory
import io.grpc.{Server, ServerBuilder}
import org.slf4j.LoggerFactory
import rpcservice.SearcherRpcServer.log
import rpcservice.protos.searcher.{SearchReply, SearchRequest, SearcherGrpc}

import scala.concurrent.{ExecutionContext, Future}

object SearcherRpcServer {

  val serverConf = ConfigFactory.load().getConfig("rpc")
  val log = LoggerFactory.getLogger(this.getClass)

  private val port = serverConf.getString("port")

  def main(args: Array[String]): Unit = {
    val server = new SearcherRpcServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()
  }
}

class SearcherRpcServer(executionContext: ExecutionContext) {self =>
  private[this] var server: Server = null

  private def start(): Unit = {
    val rpcServerBuilder = ServerBuilder.forPort(SearcherRpcServer.port.toInt)
    rpcServerBuilder.addService(SearcherGrpc.bindService(new SearcherRpcService, executionContext))
    server = rpcServerBuilder.build().start()
    log.info("Server started; listening on port: " + SearcherRpcServer.port)
    sys.addShutdownHook {
      self.stop()
      log.error("*** server shut down")
    }
  }

  private def stop(): Unit = {
    if(server != null){
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if(server != null){
      server.awaitTermination()
    }
  }

  private class SearcherRpcService extends SearcherGrpc.Searcher {
    /** Sends a greeting
     */
    override def searchLog(request: SearchRequest): Future[SearchReply] = {
      val found = SearchReply(request.name)
      Future.successful(found)
    }
  }
}
