import FinchServer.conf
import org.scalatest.funsuite.AnyFunSuite
import scalaj.http

class FinchServerTest extends AnyFunSuite{

  test("valid lambda endpoint test") {

    val lambdaConf = conf.getConfig("lambda")

    // For this interval that is not present, the response should be a HTTP 405 code
    val result = http.Http(lambdaConf.getString("apiEndpoint")).param("interval", "1900-10-10 00:00:00").param("delta", 10.toString)
      .option(http.HttpOptions.readTimeout(lambdaConf.getInt("timeout"))).asString

    assert(result.code == 405)

  }

}
