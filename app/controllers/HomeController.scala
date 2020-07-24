package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import javax.inject._
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
case class Message(value: String)

@Singleton
class HomeController @Inject()(components: ControllerComponents, @Named("library-actor") library: ActorRef)(implicit ec: ExecutionContext) extends AbstractController(components) {
  implicit val timeout: akka.util.Timeout = 1 minute

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action.async {
    // TODO: JSONで返せるようにする
    (library ? Message("ababa")).mapTo[String].map { message =>
      Ok(message)
    }
  }
}
