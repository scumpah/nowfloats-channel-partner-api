package actors

import javax.inject.Inject

import akka.actor.{Actor, ActorSystem}
import akka.kafka.ProducerSettings
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import play.api.Logger

import scala.concurrent.ExecutionContext

class TestKafkaProducerActor @Inject()(
                                        conf: Config,
                                        implicit val ec: ExecutionContext,
                                        implicit val system: ActorSystem

                                      ) extends Actor {
  private val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
  private val producer = producerSettings.createKafkaProducer()

  override def receive: Receive = {
    case record: ProducerRecord[String, String] => {
      Logger.info("received data to producer Actor")
      println("received data to producer Actor")
      producer.send(record)
      Logger.info("sent")
    }
  }
}
