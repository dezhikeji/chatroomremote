package actors.room.action

import akka.actor.{Actor, ActorRef, _}
import common.Tool._
import sdk.SystemMain

import scala.collection.mutable.{HashMap, ListBuffer}

/**
  * 房间总管
  */
class RoomActor() extends Actor with ActorLogging {
  val userCountMap = new HashMap[String, HashMap[String,Int]]()
  val roomActor = new HashMap[String, ListBuffer[ActorRef]]()
  val roomTime = new HashMap[String,Long]()

  def userCount(roomId: String) =SystemMain.roomMinCount+(userCountMap.get(roomId).map(_.size * 1.5 toInt).getOrElse(0))

  def receive = {
    case RoomIn(roomId,room:ActorRef) =>
      safe {
        log.info(s"room actor reg :  ${roomId}," + room.toString())
        if (!roomActor.contains(roomId)) {
          roomActor(roomId) = new ListBuffer[ActorRef]()
        }
        roomActor.get(roomId).map(_.append(room))
      }
    case Boadcast(id, msg, roomId) =>
      safe {
        log.info(s"room boadcast msg:  ${roomId} ,${msg.toJson()}")
        roomActor.get(roomId).map(_.foreach(_ ! Boadcast(id, msg, roomId)))
      }
    case RoomUserMessage(uid,msg:Message,roomId)=>
      safe {
        log.info(s"room user msg: ${roomId} ,${uid}  ,${msg.toJson()}")
        roomActor.get(roomId).map(_.foreach(_ ! RoomUserMessage(uid, msg, roomId)))
      }
    case RoomUserExit(id, roomId) =>
      safe {
        log.info(s"room user exit:  ${roomId} ,${id}")
        val msg = new Message(ServerMessageActionType.USER_EXIT_ROOM, HashMap("userId" -> id, "userCount" -> userCount(roomId)), System.currentTimeMillis())
        roomActor.get(roomId).map(_.foreach(_ ! Boadcast(id, msg, roomId)))
        roomActor.get(roomId).map(_.foreach(_ ! RoomUserExit(id, roomId)))
      }
    case RoomUserKick(id, actUid, text, roomId) =>
      safe {
        log.info(s"room user kick:  ${roomId} ,${id}")
        val msg = new Message(ServerMessageActionType.USER_KICK_ROOM, HashMap("userId" -> id, "userCount" -> (userCount(roomId) - 1), "actUid" -> actUid, "msgType" -> "1","contentColor"->"#FFFFFF", "text" -> text), System.currentTimeMillis())
        roomActor.get(roomId).map(_.foreach(_ ! Boadcast(id, msg, roomId)))
        roomActor.get(roomId).map(_.foreach(_ ! RoomUserKick(id, actUid,text,roomId)))
      }
    case RoomUserClientIn(uid, roomId, msg) =>
      safe {
//        log.info(s"client in  :  ${roomId} , ${uid}")
        if (!userCountMap.contains(roomId)) {
          userCountMap(roomId) = new HashMap[String, Int]()
        }
        val hasUser = if (!userCountMap(roomId).contains(uid)) {
          userCountMap(roomId).put(uid, 1)
          false
        } else {
          userCountMap(roomId).put(uid, userCountMap(roomId)(uid) + 1)
          true
        }
        if (!hasUser) {
          val level = msg.body("level").toString.toInt
          if (((userCount(roomId) < 100 && level == 0) ||  System.currentTimeMillis() > roomTime.get(roomId).getOrElse(0l)) || ((userCount(roomId) < 100 * level) && level > 0)) {
            if( level == 0) roomTime.put(roomId,System.currentTimeMillis() + SystemMain.limit)
            log.info(s"room user in:  ${roomId} ,${uid}")
            msg.body("userCount") = userCount(roomId)
            roomActor.get(roomId).map(_.foreach(_ ! Boadcast(uid, msg, roomId)))
          }
        }
      }
    case ClientLose(uid, _,roomId) =>
      safe {
        if (userCountMap.contains(roomId)) {
          if (userCountMap(roomId).contains(uid)) {
            val count = userCountMap(roomId)(uid) - 1
            if (count == 0) {
              userCountMap(roomId).remove(uid)
            } else {
              userCountMap(roomId).put(uid, count)
            }
          }
        }
      }
    case RoomClose(roomId,text) =>
      safe {
        log.info("room close:" + roomId)
        try {
          roomActor.get(roomId).map(_.foreach(_ ! RoomClose(roomId,text)))
          roomActor.remove(roomId)
        } catch {
          case _: Exception =>
        }
      }
    case _:Any=> log.error("未知信息")
  }
}
