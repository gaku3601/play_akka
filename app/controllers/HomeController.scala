package controllers

import controllers.utils.{Meta, Response}
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.LibraryService


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
case class Message(value: String)

@Singleton
class HomeController @Inject()(components: ControllerComponents, ls: LibraryService) extends AbstractController(components) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index: Action[AnyContent] = Action { implicit request =>
    ls.add("message")
    Ok(Json.toJson(Response(Meta(200))))
  }
}
