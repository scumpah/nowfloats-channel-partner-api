package controllers

import javax.inject._

import com.typesafe.config.{Config, ConfigRenderOptions}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future


@Singleton
class HomeController @Inject()(conf: Config) extends Controller {

  def version: Action[AnyContent] = Action { implicit request =>
    Ok(Json.obj("name" -> conf.getString("app.name"), "version" -> conf.getString("app.version")))
  }

  def info: Action[AnyContent] = Action.async { implicit request =>
    (request.getQueryString("username"), request.getQueryString("password")) match {
      case (Some(username), Some(password)) if username == conf.getString("api.info.username") && password == conf.getString("api.info.password") =>
        val secrets = List(conf.getString("play.crypto.secret"))
        val configString = conf.root().render(ConfigRenderOptions.concise())
        val masked: String = secrets.foldLeft[String](configString)(_.replaceAll(_, "*******"))
        Future.successful(Ok(Json.obj("config" -> Json.parse(masked))))
      case _ => version.apply(request)
    }
  }

}
