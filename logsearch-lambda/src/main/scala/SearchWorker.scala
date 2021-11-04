import java.io.{File, RandomAccessFile}
import java.lang.System.{Logger, getLogger}
import java.lang.System.Logger.Level.{ERROR, WARNING, INFO}
import java.nio.charset.StandardCharsets
import java.util.RandomAccess
import scala.util.control.Breaks.break
import org.json4s.JObject
import org.json4s.native.JsonMethods

import scala.io.Source
import scala.annotation.tailrec

// ref: https://stackoverflow.com/questions/10010151/how-to-perform-a-binary-search-of-a-text-file; https://stackoverflow.com/questions/54240131/read-until-specific-index-in-a-file-with-randomaccessfile-java

class SearchWorker {

  val logBoy = getLogger(this.getClass.getName)

  def SearchWorker{

  }

  def getLogs(file: String, verb: String, startInterval: String = "", endInterval: String = ""): List[String] = {


    logBoy.log(INFO, "Reading messages from file " + file)
    val fileToRead = new File(file)
    val raf = new RandomAccessFile(fileToRead, "r")

    val length = fileToRead.length()
    val logs = List()

    val messages = verb match {

      case "between" =>
        logBoy.log(INFO, "Reading messages between " + startInterval + " and " + endInterval)
        return List()

      case "from" =>
        logBoy.log(INFO, "Reading messages from " + startInterval + " till the end of file")
        val pos = seek(raf, startInterval, "after", length)
        raf.seek(pos)
        return readTillEnd(raf, List())

      case "to" =>
        logBoy.log(INFO, "Reading messages from the beginning of file till " + endInterval)
        return List()
      case _ => logBoy.log(ERROR, "search verb not found - " + verb)

    }
    return List()

  }

  @tailrec private def readTillEnd(raf: RandomAccessFile, messages: List[String]): List[String] = {
    val msg = raf.readLine()
    if(msg == null){
      raf.close
      return messages
    }
    readTillEnd(raf, messages:+msg)
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

      print("\nCurrently here" + lineStart)

      // Read the interval at the current position
      val line = readFilePart(raf, lineStart, 12)
      val currentInterval = new String(line, StandardCharsets.US_ASCII)
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

  def readLogs(file: File, position: Long): Array[String] = {
    new Array[String](5)
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
