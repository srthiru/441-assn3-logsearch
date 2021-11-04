import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import org.json4s.JObject
import org.json4s.native.JsonParser
import org.slf4j.LoggerFactory.getLogger

import java.io.File
import scala.io.Source

object LogSearcher extends RequestHandler[Map[String, String], Map[String, String] ]{

  val logBoy = getLogger(this.getClass)


  override def handleRequest(input: Map[String, String], context: Context): Map[String, String] = {
    val requestInterval = input.get("interval")
    val requestDelta = input.get("delta")

    val response = Map("output" -> ("Requested interval is: " + requestInterval + ", for delta: " + requestDelta))
    response
  }

  def main(args: Array[String]): Unit = {

    val worker = new SearchWorker

    val hashFile = Source.fromFile("loglocs.map")
    val hashTab = hashFile.getLines()

    // Converting string to a map - https://stackoverflow.com/questions/19938449/scala-convert-a-string-into-a-map
    val lookup = hashTab.map(_.split(": ")).map{case Array(k, v) => (k, v)}.toMap
    logBoy.info("Read lookup table")

    val requestInterval = args(0)
//    val requestDelta = args[1].toString()

    logBoy.info("Requested interval: " + requestInterval)

    val startInterval = requestInterval
    val endInterval = requestInterval

    val filePath = lookup.get(mapIntervalToKey(requestInterval)) match { case Some(e) => e}
    print("Will search in file " + filePath)

    //    val response = Map("output" -> ("Requested interval is: " + requestInterval + ", for delta: " + ""))

//    val filesToFetch = convertIntervalToFiles(requestInterval, requestDelta)
    val partialStart = worker.getLogs(filePath, "from", startInterval)
    print(partialStart)

//    if(filesToFetch.length > 1){
//      val partialEnd = getLogs(file, "to", endInterval)
//
//      val otherMessages = (1.to(filesToFetch.length-1)).map(i => readFilefromS3(filesToFetch[i]))
//    }else{
//      val messages = getLogs(file, "between", startInterval, endInterval)
//    }
//
    hashFile.close
  }

  def mapIntervalToKey(interval: String): String =
    "20211020" + interval.split(":").slice(0,2).mkString("")

//  def convertIntervalToFiles(maybeString: String, maybeString1: String): Array[String] = {
//
//  }
}