package actors

import javax.inject.{Inject, Singleton}

import akka.actor.{Actor, ActorSystem}
import akka.stream.Materializer
import database.MYSQLDao
import models.{UserTransaction, channel_url_response}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by raghav on 15/10/17.
  */
@Singleton
class ChannelHandlerActor  @Inject()(
                                    ws : WSClient,
                                    implicit val mYSQLDao: MYSQLDao
                                    )(
                                      implicit val ec: ExecutionContextExecutor,
                                      implicit val system: ActorSystem,
                                      implicit val materializer: Materializer
                                    ) extends Actor {

  override def receive : Receive = {

    case message : channel_url_response =>
      if(message.attempts < 10 ) {
        Logger.info(s"received msg $message")
        updateApiCall(message)
      }
      else {
        Logger.error("failed ")
        val dbdata = UserTransaction(message.data.UUID,message.data.channelId,message.data.channel,message.data.channelUserDataType,message.data.channelUserDataValue,message.data.channelUserDataMetaData,message.data.mobileNumber,message.data.emailId,message.data.addedDate,"Failure")
        mYSQLDao.storeUserDetails(dbdata)
        ////store data with status as failure
      }
  }


  def updateApiCall (data_object : channel_url_response) = {

    //This is just a sample test case to show how insertion works

    if(data_object.url == "https://graphapi.facebook.com/post/status"){
      Logger.info("updateAPiCall")
      Json.obj("code" -> 0, "message" -> "inserted successfully")
      val dbdata = UserTransaction(data_object.data.UUID,data_object.data.channelId,data_object.data.channel,data_object.data.channelUserDataType,data_object.data.channelUserDataValue,data_object.data.channelUserDataMetaData,data_object.data.mobileNumber,data_object.data.emailId,data_object.data.addedDate,"Success")
      mYSQLDao.storeUserDetails(dbdata)

    }
    else{
      self ! data_object.copy(attempts = data_object.attempts + 1)
      Json.obj("code" -> 1, "message" -> "insertion failed")
    }


    //The actual code for connecting to channel api's to post data

   /* ws.url(data_object.url).post(data_object.data).map {
      wsResponse: WSResponse =>
        if (wsResponse.status == 200) {
          Logger.info(s"updated ${data_object.data} data successfully with url ${data_object.url}")
          Json.obj("code" -> 0, "message" -> "inserted successfully")

          ////////////////////
          /* storing of data in DB*/
        }
        else {
          Logger.info(s"attempting again ${data_object.url} with data ${data_object.data}")
          self ! data_object.copy(attempts = data_object.attempts + 1)
          Logger.info(s"updation ${data_object.data} data failed with url ${data_object.url}")
          Json.obj("code" -> 1, "message" -> "inserted failed")
        }
    }*/
  }
}
