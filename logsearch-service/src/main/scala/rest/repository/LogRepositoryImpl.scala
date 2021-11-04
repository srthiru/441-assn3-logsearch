package rest.repository

import akka.actor.ActorSystem
import rest.data.Log

import scala.concurrent.{ExecutionContextExecutor, Future}

class LogRepositoryImpl(implicit sys: ActorSystem) extends LogRepository {

  override def findLogMessageS(interval: String): Future[List[Log]] = {
    implicit val dispatcher: ExecutionContextExecutor = sys.dispatcher
    Future(List(Log(interval)))
  }
}
