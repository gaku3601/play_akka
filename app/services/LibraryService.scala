package services

import akka.actor.ActorRef
import javax.inject.{Inject, Named}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class LibraryService @Inject()(@Named("library-actor") library: ActorRef)(implicit ec: ExecutionContext) {
  implicit val timeout: akka.util.Timeout = 1 minute

  def add(message: String): Unit = {
    print(message)
  }
}
