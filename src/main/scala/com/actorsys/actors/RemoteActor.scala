package com.actorsys.actors

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging}
import com.actorsys.service._

/**
  * Created by yuanye on 2017/2/13.
  */
class RemoteActor extends Actor with ActorLogging {
  val SUCCESS = "SUCCESS"
  val FAILURE = "FAILURE"

  override def receive: Receive = {
    case Start => log.info("receive event" + Start)
    case Stop => log.info("receive event" + Stop)
    case Shutdown(waitSecs) => {
      log.info("wait to shutdown:waitSecs:" + waitSecs)
      Thread.sleep(waitSecs)
      log.info("Shutdown this system.")
      context.system.deadLetters
    }
    case Heartbeat(id, magic) => log.info("receive Heartbeat:" + (id, magic))
    case Header(id, len, encrypted) => log.info("receive Header:" + (id, len, encrypted))
    case Packet(id, seq, content) => {
      val originalSender = sender
      log.info("receive Packet:" + (id, seq, content))
      originalSender ! (seq, SUCCESS)
    }
    case _ => log.info("ERROR")
  }
}
