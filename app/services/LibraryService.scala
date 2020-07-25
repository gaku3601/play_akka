package services

import actors.Library.{Read, Write}
import actors.Message
import akka.actor.ActorRef
import akka.parboiled2.RuleTrace.Fail
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import javax.inject.{Inject, Named}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}
import scala.language.postfixOps


class LibraryService @Inject()(@Named("library-actor") library: ActorRef)(implicit ec: ExecutionContext, ml: Materializer) {
  implicit val timeout: akka.util.Timeout = 1 minute

  def show(message: String): String = {
    val source = Source.single(Read(Message(message)))
    val flow = Flow[Read].ask[String](library)
    val sink = Sink.head[String]

    val runnable = source.via(flow).runWith(sink)

    Await.result(runnable, timeout.duration)
  }

  def post(message: String): Unit = {
    val source = Source.single(Write(Message(message)))
    val sink = Sink.actorRef[Write](library, onCompleteMessage = "stream completed", onFailureMessage = (throwable: Throwable) => Fail(throwable.getMessage))
    // TODO:同期にする
    source.to(sink).run()
  }
}
