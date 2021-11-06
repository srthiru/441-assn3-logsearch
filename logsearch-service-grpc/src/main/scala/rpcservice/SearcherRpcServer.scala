package rpcservice

import com.typesafe.config.ConfigFactory
import io.grpc.{Server, ServerBuilder}
import rpcservice.SearcherRpcServer.{logger, serverConf}
import rpcservice.protos.searcher.{SearchReply, SearchRequest, SearcherGrpc}
import scalaj.http
import scalaj.http.HttpResponse

import java.util.logging.Logger
import scala.concurrent.{ExecutionContext, Future}

object SearcherRpcServer {

  val serverConf = ConfigFactory.load()
  val logger = Logger.getLogger(this.getClass.getName)

  private val port = serverConf.getConfig("rpc").getString("port")

  def startGrpcServer(): Unit = {
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
    logger.info("Server started; listening on port: " + SearcherRpcServer.port)
    sys.addShutdownHook {
      self.stop()
      logger.severe("*** server shut down")
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

    override def searchLog(request: SearchRequest): Future[SearchReply] = {
      // Invoke the lambda function and returning the SearchReply built from the response
      logger.info("Invoking Lambda function")

      val lambdaConf = serverConf.getConfig("lambda")
      val result: HttpResponse[String] = http.Http(lambdaConf.getString("apiEndpoint")).param("interval", request.interval).param("delta", request.delta)
        .option(http.HttpOptions.readTimeout(lambdaConf.getInt("timeout"))).asString

      val out = SearchReply(result.code + ": " + result.body)

      logger.fine("Reply from lambda" + out)

      Future.successful(out)
    }
  }
}
