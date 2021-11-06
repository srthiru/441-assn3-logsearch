import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.runtime.events.{APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent}
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.typesafe.config.ConfigFactory

import java.util.logging.Logger
import java.io.{BufferedReader, InputStreamReader}
import java.security.MessageDigest
import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import scala.annotation.tailrec

object LogSearcher extends RequestHandler[APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent]{

  val logger = Logger.getLogger(this.getClass.getName)
  val conf = ConfigFactory.load().getConfig("lambdaConfig")

  val AK = conf.getString("creds.accessKey")
  val SK = conf.getString("creds.secretKey")

  val awsCreds = new BasicAWSCredentials(AK, SK)
  val provider = new AWSStaticCredentialsProvider(awsCreds)

  val s3ClientBuilder = AmazonS3ClientBuilder.standard().withForceGlobalBucketAccessEnabled(true)
  s3ClientBuilder.withRegion(Regions.US_EAST_2)
  s3ClientBuilder.setCredentials(provider)

  val s3Client = s3ClientBuilder.build()

  val bucket = conf.getString("s3.bucket")

  // Main method for testing locally
  def main(args: Array[String]): Unit ={
    logger.info(f"Starting search")
    val messages = searchLogs(conf.getString("test.interval"), conf.getInt("test.delta"))
    val md = MessageDigest.getInstance("md5")
    val reply = if(messages.length == 0) "No logs found for interval!" else md.digest(messages.mkString(";").getBytes)

    logger.info(f"Messages were retrieved: $reply")
  }

  override def handleRequest(request: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent = {

    val params = request.getQueryStringParameters
    logger.info(s"Received parameters: $params")

    val requestInterval = params.get("interval")
    val requestDelta = params.get("delta")

    logger.info(s"Received parameters: $requestInterval, $requestDelta")

    val messages = searchLogs(requestInterval, requestDelta.toInt)
    logger.fine(s"Messages found: $messages")

    val md = MessageDigest.getInstance("md5")

    val reply = if(messages.length == 0) new APIGatewayProxyResponseEvent()
      .withStatusCode(405)
      .withBody("No logs found for the given interval!") else new APIGatewayProxyResponseEvent()
      .withStatusCode(200)
      .withBody(md.digest(messages.mkString(";").getBytes).toString)

    reply
  }

  def searchLogs(interval: String, delta: Int): List[String] = {

    logger.info(s"Client configured for ${s3Client.getRegionName}")

    // Initialize worker
    val worker = new SearchWorkerS3(s3Client)

    // Load lookup table
    val hashFileKey = conf.getString("s3.hashKey")

    val hashFile = s3Client.getObject(bucket, hashFileKey)
    val reader = new BufferedReader(new InputStreamReader(hashFile.getObjectContent))
    val lookup = readFile(reader, List()).map(_.split(": ")).map{case Array(key, value) => (key, value)}.toMap

    logger.fine("Read lookup table" + lookup.toString())

    val datetime = LocalDateTime.parse(interval, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    val startInterval = datetime.minus(Duration.ofSeconds(delta))
    val endInterval = datetime.plus(Duration.ofSeconds(delta))

    logger.info(s"Looking for messages between: $startInterval and $endInterval")

    val filesToFetch = convertIntervalToFiles(datetime, delta, lookup)

    if(filesToFetch.length == 0){
      print("No messages for the interval")
      return List()
    }

    val start = startInterval.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    val end = endInterval.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

    val messages = if(filesToFetch.length > 1){
      val partialStart = worker.getLogs(bucket, filesToFetch(0), "from", start)
      val partialEnd = worker.getLogs(bucket, filesToFetch(-1), "to", end)

      val otherMessages = (1.to(filesToFetch.length - 2)).flatMap(i => {
        val fileName = filesToFetch(i)
        val logFile = s3Client.getObject(bucket, fileName)
        val reader = new BufferedReader(new InputStreamReader(logFile.getObjectContent))
        readFile(reader, List())
      }).toList

      // Stitching the results together
      partialStart ++ otherMessages ++ partialEnd

    }else{
      val messages = worker.getLogs(bucket, filesToFetch(0),"between", start, end)
      print("Messages are: " + messages)
      messages
    }

    messages

  }

  def convertIntervalToFiles(datetime: LocalDateTime, delta: Int, lookup: Map[String, String]): List[String] = {

    val files = (0 to delta/3600)
      .map(i => datetime.plus(Duration.ofHours(i)))
      .map(f => f.format(DateTimeFormatter.ofPattern("yyyyMMddHH")))
      .filter(key => lookup.contains(key)).map(f => lookup(f)).toList
//      .filter(key: String => lookup.contains(key)).map(f => lookup.get(f))

    files

  }

  @tailrec
  def readFile(reader: BufferedReader, entries: List[String]): List[String] = {

    val entry = reader.readLine

    if(entry == null){
      reader.close
      return entries
    }

    readFile(reader, entries :+ entry)
  }
}