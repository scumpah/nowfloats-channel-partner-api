package controllers

import javax.inject.Named

import akka.actor.ActorRef
import akka.actor.Status.Success
import com.google.inject.Inject
import database.MongoDao
import models.{MongoChannel, channel_data, subscriptions, update_api_request}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

/**
  * Created by raghav on 14/10/17.
  */

class UserChannelController @Inject()(
                                       @Named("TestActorHandler") testHandlerActor: ActorRef,
                                       mongoDao: MongoDao

                                     )(
                                       implicit val ec: ExecutionContextExecutor

                                     ) extends Controller {


  import models.Models._

  def userChannelData: Action[JsValue] = Action(parse.json) { implicit request =>
    request.body.validate[update_api_request].fold(
      error => {
        Logger.error(s"error $error")
        Ok(Json.obj("code" -> 1, "error" -> JsError.toJson(error)))
      },
      (request_data: update_api_request) => {
        val channelNames: List[String] = request_data.channels.map(_.channelName)
        val filteredChannels: List[MongoChannel] = Await.result(mongoDao.findChannelByChannelList(channelNames), Duration.Inf).filter {
          channelObj =>
            val messageLengthLimit: Int = channelObj.subscriptions.find(x => x.susbscriptionType.equalsIgnoreCase("PostStatus")).map(t => t.limit).getOrElse(140)
            val limitCond = if (request_data.userDataMessage.length <= messageLengthLimit) true else false
            val cond: Boolean = if (request_data.subscriptionType.equalsIgnoreCase("PostStatus")) limitCond else true
            channelObj.subscriptions.map(_.susbscriptionType).contains(request_data.subscriptionType) && cond
        }
        val wantedChannel_data: List[channel_data] = request_data.channels.filter {
          requestChannel => filteredChannels.map(_.channelName).contains(requestChannel.channelName)
        }
        val UnwantedChannelChannel_data: List[channel_data] = request_data.channels.filter {
          requestChannel => !filteredChannels.map(_.channelName).contains(requestChannel.channelName)
        }

        val freshUpdate_api_request = update_api_request(
          authenticationType = request_data.authenticationType,
          authenticationValue = request_data.authenticationValue,
          channels = wantedChannel_data,
          subscriptionType= request_data.subscriptionType,
          userDataMessage= request_data.userDataMessage,
          userDataMetadata= request_data.userDataMetadata,
          timeStamp=request_data.timeStamp,
          referrer= request_data.referrer
        )
        testHandlerActor ! freshUpdate_api_request.tojson.toString()
        val response = Json.obj(
          "code" -> 0,
          "successChannels" -> wantedChannel_data,
          "failureChannels" -> UnwantedChannelChannel_data
        )
        Ok(response)
      })

  }
}
