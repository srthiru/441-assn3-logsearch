//import rest.FinchServer
import rpcservice.SearcherRpcServer

import java.util.logging.Logger

object LogSearcherService{

  val logger = Logger.getLogger(this.getClass.getName)

  def main(args: Array[String]): Unit = {
    // Starting the servers
    logger.info("Starting the gRPC server")
    SearcherRpcServer.startGrpcServer()
  }
}