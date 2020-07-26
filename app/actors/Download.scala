package actors

import actors.Download.GetURL
import akka.actor.Actor
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport


class Download extends Actor {
  override def receive: Receive = {
    case GetURL(url: String) => {
      if (url == "error") {
        sender() ! Left("download down")
      }
      sender() ! Right("download ok")
    }
  }
}

object Download {

  case class GetURL(url: String)

}


class DownloadClient extends AbstractModule with AkkaGuiceSupport {
  override def configure = bindActor[Download]("download-actor")
}