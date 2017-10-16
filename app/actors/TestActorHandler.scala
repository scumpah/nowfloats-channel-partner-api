package actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.kafka.ProducerSettings
import akka.stream.Materializer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import play.api.Logger

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by raghav on 15/10/17.
  */
class TestActorHandler @Inject()(
                                 @Named("TestKafkaProducerActor") TestKafkaProducerActor: ActorRef
                                )(
                                  implicit val ec: ExecutionContextExecutor,
                                  implicit val system: ActorSystem,
                                  implicit val materializer: Materializer
                                ) extends Actor {

  private val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
  private val producer = producerSettings.createKafkaProducer()

  override def receive: Receive = {

    case s: String => {
      Logger.info(s"String ${s}")
      println(s"String ${s}")
      TestKafkaProducerActor ! new ProducerRecord[String, String]("test", s)
      //  TestKafkaProducerActor ! s
    }
    case i: Int => {
      // producer.send(i)
      Logger.info(s"Integer ${i}")
      println(s"Integer ${i}")
      TestKafkaProducerActor ! new ProducerRecord[String, String]("test", i.toString)
      //TestKafkaProducerActor ! i
    }
    case _ =>
      Logger.info("data sfsadf")
      println("data type not string not int")


    //  val actor1 = system.actorOf(Props[TestActorHandler], "TestActorhandler")


  }
}
