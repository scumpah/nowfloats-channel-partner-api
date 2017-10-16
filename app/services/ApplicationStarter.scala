package services

import java.time.{Clock, Instant}
import javax.inject._

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.ConsumerMessage.CommittableOffsetBatch
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import database.MongoDao
import play.modules.reactivemongo.json._
import models.{channel_url_response, kafkaData, _}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.Try
import models.Models._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection
import slick.dbio.Effect.Read

@Singleton
class ApplicationStarter @Inject()(
                                    clock: Clock,
                                    appLifecycle: ApplicationLifecycle,
                                    mongoDao: MongoDao,
                                    @Named("ChannelHandlerActor") channelHandlerActor: ActorRef,
                                    ws: WSClient
                                  )(
                                    implicit val ec: ExecutionContextExecutor,
                                    implicit val system: ActorSystem,
                                    implicit val materializer: Materializer
                                  ) {

  def apiOfChannelAndSub(channel: String, subject_type: String) = {
    Logger.info(s"qwertyui$channel$subject_type")
    val channeldata: Future[Option[MongoChannel]] = mongoDao.findChannel(channel)
    channeldata.map { cd =>
      Logger.info(s"data from mongo $cd")
      cd.get.subscriptions.filter(x => x.susbscriptionType == subject_type)
    }
    // get api based on channel and subject type
  }


  system.scheduler.scheduleOnce(10.second, new Runnable {
    override def run(): Unit = {
      val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withGroupId("test")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      Consumer.committableSource(consumerSettings, Subscriptions.topics("test"))
        .mapAsync(1) { msg =>
          Logger.info(s"offset->${msg.record.offset()},partition->${msg.record.partition()},key->${msg.record.key()},value->${msg.record.value()},checksum->${msg.record.checksum()},timestamp->${msg.record.timestamp()}")
          try {
            Json.parse(msg.record.value())
              .validate[update_api_request]
              .fold(error => {
                Logger.error(s"json error $error")
                Logger.error(s"error ${JsError.toJson(error)}")
              }, test_data => {
                try {
                  Logger.info("yszudx")
                  val channels = test_data.channels
                  channels.foreach(channel => {
                    val subject_type = test_data.subscriptionType
                    //subject data can be image url or video or message
                    val sub_data = test_data.userDataMessage
                    //metadata will include description while sharing a link
                    val sub_metadata = test_data.userDataMetadata
                    val referrer = test_data.referrer
                    val channelid = channel.channelId
                    val channelname = channel.channelName
                    val data = kafkaData("98323512132", channel.channelId, channel.channelName, subject_type, sub_data, sub_metadata, "9876543210", "test@gmail.com", "Inprogress", referrer)
                    val subdata = apiOfChannelAndSub(channel.channelName, subject_type)
                    subdata.map {
                      cr =>
                        Logger.info(s"pushing to url ${cr.head.subscriptionUrl}")
                        Logger.info(s"pushing to channelHandlerActor $subdata")
                        channelHandlerActor ! channel_url_response(data, cr.head.subscriptionUrl)
                    }
                  })
                }
                catch {
                  case ex: Exception => Logger.error(s"error mong $ex")
                }
              })
          }
          catch {
            case ex: Exception => Logger.error(s"error 1 $ex")
          }
          Future.successful(Done).map(_ => msg.committableOffset)
        }
        .groupedWithin(10, 5.seconds)
        .map(group => group.foldLeft(CommittableOffsetBatch.empty) { (batch, elem) => batch.updated(elem) })
        .mapAsync(3)(b => try {
          b.commitScaladsl()
        } catch {
          case e: Exception =>
            Logger.error("error while committing kafka offset, retrying", e)
            Try {
              b.commitScaladsl()
            }.toOption
              .getOrElse[Future[Done]](Future(Done))
        })
        .runWith(Sink.ignore)
    }
  })


  private val start: Instant = clock.instant
  Logger.info(s"Application : Starting application at $start.")

  appLifecycle.addStopHook { () =>
    val stop: Instant = clock.instant
    val runningTime: Long = stop.getEpochSecond - start.getEpochSecond
    Logger.info(s"Application : Stopping application at ${clock.instant} after ${runningTime}s.")
    Future.successful(())
  }
}
