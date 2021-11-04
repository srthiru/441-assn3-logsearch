import com.typesafe.config.ConfigFactory
import scalaj.http.Http
import com.google.gson.Gson
import scalapb.json4s.JsonFormat

object RestClient {
//  val config = ConfigFactory.load().getConfig("server")
//
//  val url = config.getString("apiurl")
//  val func = config.getString("serviceName")

  val gson = new Gson

  def main(args: Array[String]): Unit ={
    val request = Http("http://127.0.0.1:3000/").headers(Map(
      "Content-Type" -> "application/json"
    ))
      .timeout(2000, 5000)
      .postData(gson.toJson(Map("requestMessage" -> "abc")))

    val response = request.asString

    println(response)
  }
}
