package modules

import actors._
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * Created by raghav on 15/10/16.
  */
class ActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[TestActorHandler]("TestActorHandler")
    bindActor[TestKafkaProducerActor]("TestKafkaProducerActor")
    bindActor[ChannelHandlerActor]("ChannelHandlerActor")


  }
}
