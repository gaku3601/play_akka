package actors

import actors.Library.{Read, Write}
import akka.actor.Actor
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

case class Message(value: String)

class Library extends Actor {
  override def receive: Receive = {
    // TODO: エラーハンドリングを記述し、仮に例外を発生した場合、onComplete等で補足できないか検証する
    case Read(message) => {
      sender ! s"I receive '${message.value}'!"
    }
    case Write(message) => {
      println(s"I receive '${message.value}'!")
    }
  }
}

object Library {

  case class Read(message: Message)

  case class Write(message: Message)

}


class LibraryClient extends AbstractModule with AkkaGuiceSupport {
  override def configure = bindActor[Library]("library-actor")
}