package controllers.utils

import play.api.libs.json._

case class Meta(status: Int, errorMessage: Option[String] = None)

object Meta {
  implicit val writes: Writes[Meta] = Json.writes[Meta]
}