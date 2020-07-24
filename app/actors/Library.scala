package actors

import akka.actor.Actor
import com.google.inject.AbstractModule
import controllers.Message
import play.api.libs.concurrent.AkkaGuiceSupport

class Library extends Actor {
  override def receive: Receive = {
    case Message(message) => {
      sender ! s"I receive '$message'"
    }
  }
}

class LibraryClient extends AbstractModule with AkkaGuiceSupport {
  override def configure = bindActor[Library]("library-actor")
}