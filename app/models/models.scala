package models

import java.time.{LocalDateTime, ZoneId}

import play.api.libs.json.{JsValue, Json, OFormat}

import scala.concurrent.Promise

/**
  * Created by raghav on 15/10/17.
  */
case class channel_request(
                            channel: String,
                            subject_type: String,
                            subject_value: String
                          )

case class channel_url_response(data: kafkaData, url: String, attempts: Int = 0, promise: Promise[JsValue] = Promise[JsValue]())
case class kafkaData(
                      UUID : String,
                      channelId : String,
                      channel:String,
                      channelUserDataType:String,
                      channelUserDataValue:String,
                      channelUserDataMetaData:String,
                      mobileNumber:String,
                      emailId:String,
                      transactionStatus:String,
                      referrer:String,
                      addedDate:LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                    )

case class update_api_request(
                               authenticationType: String,
                               authenticationValue: String,
                               channels: List[channel_data],
                               subscriptionType: String,
                               userDataMessage: String,
                               userDataMetadata: String,
                               timeStamp: String,
                               referrer: String
                             ){
  def tojson = Json.obj(
    "authenticationType"  -> authenticationType,
    "authenticationValue" -> authenticationValue,
    "channels" -> channels.map(x=>x.tojson),
    "subscriptionType" -> subscriptionType,
    "userDataMessage" -> userDataMessage,
    "userDataMetadata" -> userDataMetadata,
    "timeStamp" -> timeStamp,
    "referrer" -> referrer
  )
}

case class channel_data(
                         channelId: String,
                         channelName: String
                       ){
  def tojson = Json.obj(
    "channelId" -> channelId,
    "channelName" -> channelName
  )
}



case class subscriptions(
                          subscriptionId: String,
                          susbscriptionType: String,
                          subscriptionName: String,
                          limit: Int,
                          subscriptionUrl: String,
                          subscriptionUrlType: String
                        )

case class MongoChannel(channelId: String,
                        channelName: String,
                        categoryCode: String,
                        categoryName: String,
                        appId: String,
                        subscriptions: List[subscriptions])

case class UserTransactionInfo(
                            var UUID: String,
                            var channelId: String,
                            var channel: String,
                            var channelUserDataType: String,
                            var channelUserDataValue: String,
                            var channelUserDataMetaData: String,
                            var mobileNumber: String,
                            var emailId: String,
                            var addedDate: LocalDateTime,
                            var transactionStatus: String,
                            var updatedDate:LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata")),
                            var transactionId: Int=0
                          )
case class UserTransaction(
                                var UUID: String,
                                var channelId: String,
                                var channel: String,
                                var channelUserDataType: String,
                                var channelUserDataValue: String,
                                var channelUserDataMetaData: String,
                                var mobileNumber: String,
                                var emailId: String,
                                var addedDate: LocalDateTime,
                                var transactionStatus: String,
                                var updatedDate:LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                              )


object Models {
  implicit val subscriptionsF: OFormat[subscriptions] = Json.format[subscriptions]
  implicit val MongoChannelF: OFormat[MongoChannel] = Json.format[MongoChannel]
  implicit val channel_dataF: OFormat[channel_data] = Json.format[channel_data]
  implicit val update_api_requestF: OFormat[update_api_request] = Json.format[update_api_request]
  implicit val userTransactionInfoFmt = Json.format[UserTransactionInfo]

}