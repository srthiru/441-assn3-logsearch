package rest.repository

import scala.concurrent.Future
import rest.data.Log

trait LogRepository {
  def findLogMessageS(interval: String):
  Future[List[Log]]
}
