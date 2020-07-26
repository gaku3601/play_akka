package controllers

import javax.inject._
import play.api.mvc._
import services.LibraryService

import scala.concurrent.ExecutionContext


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

@Singleton
class HomeController @Inject()(components: ControllerComponents, ls: LibraryService)(implicit ec: ExecutionContext) extends AbstractController(components) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  /*
def index: Action[AnyContent] = Action.async { implicit request =>

  /*
  ls.show("error1") map { x =>
    Ok(Json.toJson(Response(Meta(200), Some(Json.toJson(x)))))
  }
   */
  ls.post("error1") map {
    case Right(_) => Ok(Json.toJson(Response(Meta(200), Some(Json.toJson("ok")))))
    case Left(b) => BadRequest(Json.toJson(b))
  }
}

   */
  def index: Action[AnyContent] = Action { implicit request =>

    /*
    ls.show("error1") map { x =>
      Ok(Json.toJson(Response(Meta(200), Some(Json.toJson(x)))))
    }
     */
    ls.graph("error1")
    Ok("ok")
  }
}
