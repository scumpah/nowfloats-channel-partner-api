package database

import javax.inject.Named

import akka.actor.ActorRef
import com.google.inject.{Inject, Singleton}
import com.typesafe.config.Config
import models.MongoChannel
import play.api.libs.ws.WSClient
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection
import play.modules.reactivemongo.json._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import models.Models._
import play.api.libs.json.Json
/**
  * Created by raghav on 15/10/17.
  */
@Singleton
class MongoDao @Inject()(
                          conf: Config, ws: WSClient,
                          reactiveMongoApi: ReactiveMongoApi
                        )(implicit val ec: ExecutionContextExecutor
                        ) {
  private val channelCollection: JSONCollection = Await.result(reactiveMongoApi.database.map(_.collection[reactivemongo.play.json.collection.JSONCollection]("channeldata")), 10.seconds)

  def findChannel(channel: String): Future[Option[MongoChannel]] = {
    channelCollection.find(Json.obj("channelName" → channel)).one[MongoChannel]
  }

  def findChannelByChannelList(channels: List[String]): Future[List[MongoChannel]] = {
    channelCollection.find(Json.obj("channelName" → Json.obj("$in" -> channels))).cursor[MongoChannel]().collect[List]()
  }

}
