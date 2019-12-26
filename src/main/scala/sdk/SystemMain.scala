package sdk

import actors.room.action.RoomActor
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by admin on 2016/9/9.
  */
object SystemMain {
  val system = ActorSystem.create("room")
  val conf = ConfigFactory.load()
  val limit=conf.getInt("msgLimittime")
  val roomMinCount=conf.getInt("roomMinCount")
  def main(args: Array[String]) {
    //启动消息发送Actor
    val roomActor = system.actorOf(Props[RoomActor], "room")
  }

}
