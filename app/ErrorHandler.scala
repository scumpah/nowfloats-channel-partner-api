/**
  * Created by raghav on 15/10/17.
  */

import javax.inject._

import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router

import scala.concurrent._


@Singleton
class ErrorHandler @Inject()(
                              env: Environment,
                              config: Configuration,
                              sourceMapper: OptionalSourceMapper,
                              router: Provider[Router]
                            ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    Future.successful(Ok(Json.obj("code" -> 99, "error" -> exception.getLocalizedMessage)))
  }

  override def onForbidden(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(Ok(Json.obj("code" -> 403, "error" -> message)))
  }

  override def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(Ok(Json.obj("code" -> 400, "error" -> message)))
  }

  override def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(Ok(Json.obj("code" -> 404, "error" -> message)))
  }

  override def onOtherClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(Ok(Json.obj("code" -> statusCode, "error" -> message)))
  }
}