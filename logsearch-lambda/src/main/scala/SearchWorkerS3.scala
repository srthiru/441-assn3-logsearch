import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.{GetObjectRequest, S3Object}
import com.typesafe.config.ConfigFactory

import java.io.{BufferedReader, IOException, InputStream, InputStreamReader}
import java.util.logging.Logger
import scala.annotation.tailrec


// Worker class that performs binary search on a given S3 file
class SearchWorkerS3(s3Client: AmazonS3) {

  val logger = Logger.getLogger(this.getClass.getName)

  def getLogs(bucket: String, key: String, verb: String, startInterval: String = "", endInterval: String = ""): List[String] = {

    print("Reading messages from file "+bucket+key)

    val conf = ConfigFactory.load().getConfig("lambdaConfig")

    // Get file length
    val s3Object: S3Object = s3Client.getObject(bucket, key)
    val fileLength = s3Object.getObjectMetadata.getContentLength

    val maxLineLength = conf.getInt("log.maxLineLength")

    val messages: List[String] = verb match {

      case "between" =>
        val pos = seek(startInterval, fileLength, maxLineLength, bucket, key)
        logger.fine("Reading messages between " + startInterval + " and " + endInterval)

        val rangeObjectRequest = new GetObjectRequest(bucket, key).withRange(pos, fileLength)
        val objectPortion = s3Client.getObject(rangeObjectRequest)

        val reader = new BufferedReader(new InputStreamReader(objectPortion.getObjectContent))

        val messages = readUntil(reader, List(), endInterval)
        messages

      case "from" =>
        val pos = seek(startInterval, fileLength, maxLineLength, bucket, key)
        logger.info("Reading until " + startInterval)
        val rangeObjectRequest = new GetObjectRequest(bucket, key).withRange(pos, pos+fileLength)
        val objectPortion = s3Client.getObject(rangeObjectRequest)

        val reader = new BufferedReader(new InputStreamReader(objectPortion.getObjectContent))

        val messages = readUntil(reader, List())
        logger.info(f"Read ${messages.length} messages between interval")
        messages

      case "to" =>
        logger.info("Reading messages from the beginning of file till " + endInterval)
        val rangeObjectRequest = new GetObjectRequest(bucket, key).withRange(0, fileLength)
        val objectPortion = s3Client.getObject(rangeObjectRequest)

        val reader = new BufferedReader(new InputStreamReader(objectPortion.getObjectContent))

        val messages = readUntil(reader, List(), endInterval)
        messages

      case _ =>
        logger.severe("search verb not found - " + verb)
        List()

    }

    messages

  }

  // Tail recursion function to read until a given endString interval or till the end of file
  @tailrec private def readUntil(reader: BufferedReader, messages: List[String], endString: String = null): List[String] ={

    val msg = reader.readLine

    if(msg == null){
//      reader.close
      return messages
    }

    print(s"read line: $msg")

    if(endString != null)
      if(msg.substring(0, 12).compareTo(endString) > 0){
//        reader.close
        return messages
      }
    readUntil(reader, messages:+msg, endString)
  }

  def seek(searchValue: String, length: Long, maxLineLength: Int, bucket: String, key: String): Long = {
    var min = 0.toLong
    var max = length
    var direction = 0
    var counter = 0
    var lineStart: Long = 0
    var prevMiddle: Long = 0

    while(min < max & counter < 60) {
      val middle = (min+max)/2
      lineStart = findNextLine(middle, maxLineLength, bucket, key)
      print(f"\nIteration $counter, min: $min, max: $max, lineStart: $lineStart, middle: $middle")

      if (prevMiddle == middle)
        return lineStart

      val currentInterval = getInterval(lineStart, 12, bucket, key)
      println("\nFound interval: " + currentInterval + "; To Search: " + searchValue + "; Direction: " + currentInterval.compareTo(searchValue))

      // Compare it to the search query interval
      direction = currentInterval.compareTo(searchValue)
      print("Direction: " + direction)
      // If compare value is 0, then the interval has an exact match
      if (direction == 0) {
        print("Matched")
        return lineStart
      }
      // If the compare value is +ve, then the query interval is before the current position
      if (direction > 0) {
        print("\nIs before")
        max = middle
      }
      // If the compare value is -ve, then the query interval is after the current position
      else {
        print("\nIs after")
        min = middle
      }
      counter += 1
      prevMiddle = middle
    }
    print(f"Starting Interval Found at: $lineStart")
    lineStart
  }

  def findNextLine(position: Long, maxlineLength: Long, bucket: String, key: String): Long ={
    val rangeObjectRequest = new GetObjectRequest(bucket, key).withRange(position, position+maxlineLength)
    val objectPortion = s3Client.getObject(rangeObjectRequest)

    var pos = 0

    val reader = new BufferedReader(new InputStreamReader(objectPortion.getObjectContent))
    while(reader.read() != '\n') pos += 1

//    objectPortion.close

    position + pos + 1
  }

  def getInterval(position: Long, length: Int, bucket: String, key: String): String = {
    val rangeObjectRequest = new GetObjectRequest(bucket, key).withRange(position, position+length)
    val objectPortion = s3Client.getObject(rangeObjectRequest)

    val reader = new BufferedReader(new InputStreamReader(objectPortion.getObjectContent))

    val interval = (1 to length).map(i => reader.read().toChar).mkString("")
//    objectPortion.close

    print(s"This is the found interval $interval")

    interval
  }

  @throws[IOException]
  private def displayTextInputStream(input: InputStream): Unit = { // Read the text input stream one line at a time and display each line.
    val reader = new BufferedReader(new InputStreamReader(input))
    var line = reader.readLine
    while (true){
      val line = reader.readLine
      if(line == null) return
      print(line)
    }
    System.out.println()
  }
}
