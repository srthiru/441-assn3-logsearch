import org.scalatest.funsuite.AnyFunSuite
import rpcservice.SearcherRpcServer.serverConf
import scalaj.http

class LogSearcherServiceTest extends AnyFunSuite{

  test("valid endpoint test") {

    val lambdaConf = serverConf.getConfig("lambda")

    // For this interval that is not present, the response should be a HTTP 405 code
    val result = http.Http(lambdaConf.getString("apiEndpoint")).param("interval", "1900-10-10 00:00:00").param("delta", 10.toString)
      .option(http.HttpOptions.readTimeout(lambdaConf.getInt("timeout"))).asString

    assert(result.code == 405)
  }

}
