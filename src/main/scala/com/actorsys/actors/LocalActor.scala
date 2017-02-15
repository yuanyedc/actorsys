package com.actorsys.actors

import akka.actor.{Actor, ActorLogging}
import com.actorsys.service._

/**
  * Created by yuanye on 2017/2/13.
  */
class LocalActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case Start => log.info("start")
    case Stop => log.info("stop")
    case Heartbeat(id, magic) => log.info("heartbeat" + (id, magic))
    case Header(id, len, encrypted) => log.info("header" + (id, len, encrypted))
    case Packet(id, seq, content) => log.info("packet" + (id, seq, content))
    case _ => log.info("error")
  }
}
