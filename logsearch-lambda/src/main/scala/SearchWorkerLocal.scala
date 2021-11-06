import java.io.{File, RandomAccessFile}
import java.nio.charset.StandardCharsets
import java.util.logging.Logger
import scala.annotation.tailrec


// Please ignore this file, was used for testing binary search locally using RandomAccessFile
class SearchWorker {

  val logger = Logger.getLogger(this.getClass.getName)

  def SearchWorker{

  }

  def getLogs(file: String, verb: String, startInterval: String = "", endInterval: String = ""): List[String] = {

    logger.info("Reading messages from file " + file)
    val fileToRead = new File(file)
    val raf = new RandomAccessFile(fileToRead, "r")

    val length = fileToRead.length()

    val messages: List[String] = verb match {

      case "between" =>
        val pos = seek(raf, startInterval, "after", length)
        print("Reading messages between " + startInterval + " and " + endInterval)
        raf.seek(pos)
        val messages = readUntil(raf, List(), endInterval)
        messages

      case "from" =>
        val pos = seek(raf, startInterval, "after", length)
        logger.info("Reading until " + startInterval)
        raf.seek(pos)
        val messages = readUntil(raf, List())
        logger.info(f"Read ${messages.length} messages between interval")
        messages

      case "to" =>
        logger.info("Reading messages from the beginning of file till " + endInterval)
        raf.seek(0)
        val messages = readUntil(raf, List(), endInterval)
        messages

      case _ => logger.severe("search verb not found - " + verb)
                List()

    }

    messages

  }

  @tailrec private def readUntil(raf: RandomAccessFile, messages: List[String], endString: String = null): List[String] = {
    val msg = raf.readLine()
    if(msg == null) {
      raf.close
      return messages
    }
    print(f"reading message $msg; interaval: ${msg.substring(0, 12)}; INTERVAL DIRECTION: ${msg.substring(0,12).compareTo(endString)}")
    if(endString != null)
      if(msg.substring(0,12).compareTo(endString) > 0) {
        raf.close
        return messages
      }
    readUntil(raf, messages:+msg, endString)
  }

  def seek(raf: RandomAccessFile, searchValue: String, where: String, length: Long): Long = {

    // Using some vars here to keep track of search variables
    var min = 0.toLong
    var max = length
    var direction = 0
    var counter = 0
    var lineStart: Long = 0
    var prevMiddle: Long = 0

    // Binary search with slight modification to find the next occurring line from a random middle seek
    // TODO: Consider converting into a tailrec function to avoid looping
    while(min < max & counter < 60) {
      // Go to the middle and find the next newline character
      val middle = (min + max) / 2
      print(f"\nIteration $counter, min: $min, max: $max, lineStart: $lineStart, middle: $middle")
      lineStart = findNextLine(raf, middle)
      if (prevMiddle == middle)
        return lineStart

//      print("\nCurrently here" + lineStart)

      // Read the interval at the current position
      val line = readFilePart(raf, lineStart, 12)
      val currentInterval = new String(line, StandardCharsets.US_ASCII)
//      println("\nFound interval: " + currentInterval + "; To Search: " + searchValue + "; Direction: " + currentInterval.compareTo(searchValue))

      // Compare it to the search query interval
      direction = currentInterval.compareTo(searchValue)
//      print("Direction: " + direction)

      // If compare value is 0, then the interval has an exact match
      if (direction == 0) {
//        print("Matched")
        return lineStart
      }
      // If the compare value is +ve, then the query interval is before the current position
      if (direction > 0) {
//        print("\nIs before")
        max = middle
      }
      // If the compare value is -ve, then the query interval is after the current position
      else {
//        print("\nIs after")
        min = middle
      }
      counter += 1
      prevMiddle = middle
    }
    print(f"Starting Interval Found at: $lineStart")
    lineStart
  }

  def findNextLine(raf: RandomAccessFile, position: Long): Long = {

    raf.seek(position);
    var pos = 0

    while(true){
      val c = raf.read()
      if (c == '\n') return position + pos + 1
      pos += 1
    }

    position + pos + 1
  }

  def readFilePart(raf: RandomAccessFile, start: Long, l: Int): Array[Byte] = try {
    print("Reading next " + l + "bytes")
    val buf = new Array[Byte](l)
    raf.seek(start)
    raf.readFully(buf)
    buf
  } catch {
    case __e: Exception =>
      throw __e
  }

}
