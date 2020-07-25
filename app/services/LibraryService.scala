package services

import actors.Library.{Read, Write}
import actors.Message
import akka.actor.ActorRef
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import javax.inject.{Inject, Named}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps


class LibraryService @Inject()(@Named("library-actor") library: ActorRef)(implicit ec: ExecutionContext, ml: Materializer) {
  implicit val timeout: akka.util.Timeout = 5 second

  def show(message: String): Future[String] = {
    val source = Source.single(Read(Message(message)))
    val flow = Flow[Read].ask[String](library)
    val sink = Sink.head[String]
    source.via(flow).runWith(sink)
  }

  def post(message: String): Future[Either[String, String]] = {
    //val sink = Sink.actorRef[Write](library, onCompleteMessage = "stream completed", onFailureMessage = (throwable: Throwable) => Fail(throwable.getMessage))
    val source = Source.single(Write(Message(message)))
    val flow = Flow[Write].ask[Either[String, String]](library)
    val sink = Sink.head[Either[String, String]]
    source.via(flow).runWith(sink)
  }
}
