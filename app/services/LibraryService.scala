package services

import actors.Download.GetURL
import actors.Library.{Read, Write}
import actors.Message
import akka.actor.ActorRef
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import javax.inject.{Inject, Named}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps


class LibraryService @Inject()(@Named("library-actor") library: ActorRef, @Named("download-actor") download: ActorRef)(implicit ec: ExecutionContext, ml: Materializer) {
  implicit val timeout: akka.util.Timeout = 5 second

  def show(message: String): Future[String] = {
    val source = Source.single(Read(Message(message)))
    val flow = Flow[Read].ask[String](library)
    val sink = Sink.head[String]
    source.via(flow).runWith(sink)
  }

  def post(message: String): Unit = {
    //val sink = Sink.actorRef[Write](library, onCompleteMessage = "stream completed", onFailureMessage = (throwable: Throwable) => Fail(throwable.getMessage))
    val sink = Sink.head[Either[String, String]]
    val source = Source.single(GetURL("downloadUrl"))
    val flow1 = Flow[GetURL].ask[Either[String, String]](download) map {
      case Right(_) => Write(Message("test"))
      case Left(_) => throw new Exception("error")
    }
    val flow2 = Flow[Write].ask[Either[String, String]](library)
    val test = source.via(flow1).via(flow2).runWith(sink)
    println(Await.result(test, timeout.duration))
  }
}
