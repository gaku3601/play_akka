package services

import actors.Download.GetURL
import actors.Library.{Read, Write}
import actors.Message
import akka.actor.ActorRef
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import akka.stream.{ClosedShape, Materializer}
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

  def graph(message: String): Unit = {
    val out = Sink.head[Either[String, String]]
    val g = RunnableGraph.fromGraph(GraphDSL.create(out) { implicit builder =>
      out =>
        import GraphDSL.Implicits._
        val in = Source.single(GetURL("downloadUrl"))
        val flow1 = Flow[GetURL].ask[Either[String, String]](download)
        val bcast = builder.add(Broadcast[Either[String, String]](outputPorts = 2))
        val leftOut = Flow[Either[String, String]].filter(_.isLeft)
        val rightOut = Flow[Either[String, String]].filter(_.isRight)
        val merge = builder.add(Merge[Either[String, String]](2))
        val converter = Flow[Either[String, String]].map(x => {
          Write(Message(x.getOrElse("error")))
        })
        val flow2 = Flow[Write].ask[Either[String, String]](library)

        in ~> flow1 ~> bcast ~> leftOut ~> merge ~> out
        bcast ~> rightOut ~> converter ~> flow2 ~> merge
        ClosedShape
    }).run()
    println(Await.result(g, timeout.duration))
  }

  // 最終完成形: これを元にakka streamは使っていく
  def graph2(message: String) = {
    val sink = Sink.head[Either[String, String]]
    RunnableGraph.fromGraph(GraphDSL.create(sink) { implicit builder =>
      out =>
        import GraphDSL.Implicits._
        val broadcast = builder.add(Broadcast[Either[String, String]](2))
        val merge = builder.add(Merge[Either[String, String]](2))
        val in = Source.single(GetURL(message))
        val flow1 = Flow[GetURL].ask[Either[String, String]](download)
        val converter = Flow[Either[String, String]].map(x => Write(Message(x.getOrElse("error"))))
        val flow2 = Flow[Write].ask[Either[String, String]](library)

        in ~> flow1 ~> broadcast.in
        broadcast.out(0).filter(_.isLeft) ~> merge.in(0)
        broadcast.out(1).filter(_.isRight) ~> converter ~> flow2 ~> merge.in(1)
        merge.out ~> out
        ClosedShape
    }).run()
  }
}
